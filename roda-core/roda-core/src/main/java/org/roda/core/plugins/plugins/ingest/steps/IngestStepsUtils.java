package org.roda.core.plugins.plugins.ingest.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roda.core.RodaCoreFactory;
import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.InvalidParameterException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.RequestNotValidException;
import org.roda.core.data.utils.JsonUtils;
import org.roda.core.data.v2.LiteOptionalWithCause;
import org.roda.core.data.v2.ip.AIP;
import org.roda.core.data.v2.ip.AIPState;
import org.roda.core.data.v2.ip.TransferredResource;
import org.roda.core.data.v2.jobs.Job;
import org.roda.core.data.v2.jobs.Report;
import org.roda.core.model.LiteRODAObjectFactory;
import org.roda.core.plugins.Plugin;
import org.roda.core.plugins.PluginException;
import org.roda.core.plugins.orchestrate.IngestJobPluginInfo;
import org.roda.core.plugins.plugins.PluginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IngestStepsUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(IngestStepsUtils.class);

  public static void executePlugin(IngestExecutePack pack, IngestStep step) {
    if (!step.needsAips() || !pack.getAips().isEmpty()) {
      Report pluginReport = IngestStepsUtils.executeStep(pack, step.getParameters(), step.getPluginName());
      mergeReports(pack.getJobPluginInfo(), pluginReport);
      if (step.needsAips()) {
        recalculateAIPsList(pack, step.removesAips());
      }
    }
  }

  public static Report executeStep(IngestExecutePack pack, Map<String, String> params, String pluginName) {
    Plugin<AIP> plugin = RodaCoreFactory.getPluginManager().getPlugin(pluginName, AIP.class);
    Map<String, String> mergedParams = new HashMap<>(pack.getParameterValues());
    if (params != null) {
      mergedParams.putAll(params);
    }

    // set outcome_object_id > source_object_id relation
    mergedParams.put(RodaConstants.PLUGIN_PARAMS_OUTCOMEOBJECTID_TO_SOURCEOBJECTID_MAP,
      JsonUtils.getJsonFromObject(pack.getJobPluginInfo().getAipIdToTransferredResourceIds()));

    try {
      plugin.setParameterValues(mergedParams);
      List<LiteOptionalWithCause> lites = LiteRODAObjectFactory.transformIntoLiteWithCause(pack.getModel(),
        pack.getAips());
      return plugin.execute(pack.getIndex(), pack.getModel(), pack.getStorage(), lites);
    } catch (InvalidParameterException | PluginException | RuntimeException e) {
      LOGGER.error("Error executing plugin: {}", pluginName, e);
    }

    return null;
  }

  public static void mergeReports(IngestJobPluginInfo jobPluginInfo, Report pluginReport) {
    if (pluginReport != null) {
      for (Report reportItem : pluginReport.getReports()) {
        if (TransferredResource.class.getName().equals(reportItem.getSourceObjectClass())) {
          Report report = new Report(reportItem);
          report.addReport(reportItem);
          jobPluginInfo.addReport(report, false);
        } else {
          jobPluginInfo.addReport(reportItem, true);
        }
      }
    }
  }

  /**
   * Recalculates (if failures must be noticed) and updates AIP objects (by
   * obtaining them from model)
   */
  public static void recalculateAIPsList(IngestExecutePack pack, boolean removeAIPProcessingFailed) {
    pack.getAips().clear();
    Set<String> aipsToReturn = new HashSet<>();
    Set<String> transferredResourceAips;
    List<String> transferredResourcesToRemoveFromjobPluginInfo = new ArrayList<>();
    boolean oneTransferredResourceAipFailed;
    IngestJobPluginInfo jobPluginInfo = pack.getJobPluginInfo();

    for (Map.Entry<String, Map<String, Report>> transferredResourcejobPluginInfoEntry : jobPluginInfo
      .getReportsFromBeingProcessed().entrySet()) {
      String transferredResourceId = transferredResourcejobPluginInfoEntry.getKey();
      transferredResourceAips = new HashSet<>();
      oneTransferredResourceAipFailed = false;

      if (jobPluginInfo.getAipIds(transferredResourceId) != null) {
        for (String aipId : jobPluginInfo.getAipIds(transferredResourceId)) {
          Report aipReport = transferredResourcejobPluginInfoEntry.getValue().get(aipId);
          if (aipReport.getPluginState() == Report.PluginState.FAILURE) {
            LOGGER.trace("Removing AIP {} from the list", aipReport.getOutcomeObjectId());
            oneTransferredResourceAipFailed = true;
            break;
          } else {
            transferredResourceAips.add(aipId);
          }
        }

        if (oneTransferredResourceAipFailed) {
          LOGGER.info(
            "Will not process AIPs from transferred resource '{}' any longer because at least one of them failed",
            transferredResourceId);
          jobPluginInfo.incrementObjectsProcessedWithFailure();
          jobPluginInfo.failOtherTransferredResourceAIPs(pack.getModel(), pack.getIndex(), transferredResourceId);
          transferredResourcesToRemoveFromjobPluginInfo.add(transferredResourceId);
        } else {
          aipsToReturn.addAll(transferredResourceAips);
        }
      }
    }

    if (removeAIPProcessingFailed) {
      for (String transferredResourceId : transferredResourcesToRemoveFromjobPluginInfo) {
        jobPluginInfo.remove(transferredResourceId);
      }
    }

    for (String aipId : aipsToReturn) {
      try {
        pack.getAips().add(pack.getModel().retrieveAIP(aipId));
      } catch (RequestNotValidException | NotFoundException | GenericException | AuthorizationDeniedException e) {
        LOGGER.error("Error while retrieving AIP", e);
      }
    }
  }

  public static void updateAIPsToBeAppraised(IngestExecutePack pack, Job cachedJob) {
    for (AIP aip : pack.getAips()) {
      aip.setState(AIPState.UNDER_APPRAISAL);
      try {
        aip = pack.getModel().updateAIPState(aip, cachedJob.getUsername());

        pack.getParameterValues().put(RodaConstants.PLUGIN_PARAMS_OUTCOMEOBJECTID_TO_SOURCEOBJECTID_MAP,
          JsonUtils.getJsonFromObject(pack.getJobPluginInfo().getAipIdToTransferredResourceIds()));

        // update main report outcomeObjectState
        PluginHelper.updateJobReportState(pack.getIngestPlugin(), pack.getModel(), aip.getIngestSIPUUID(), aip.getId(),
          AIPState.UNDER_APPRAISAL, cachedJob);

        // update counters of manual intervention
        pack.getJobPluginInfo().incrementOutcomeObjectsWithManualIntervention();
      } catch (GenericException | NotFoundException | RequestNotValidException | AuthorizationDeniedException e) {
        LOGGER.error("Error while updating AIP state to '{}'. Reason: {}", AIPState.UNDER_APPRAISAL, e.getMessage());
      }
    }
  }
}