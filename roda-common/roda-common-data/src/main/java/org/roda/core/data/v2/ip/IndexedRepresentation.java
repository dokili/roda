/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.data.v2.ip;

import org.roda.core.data.v2.index.IsIndexed;

public class IndexedRepresentation extends Representation implements IsIndexed {

  private static final long serialVersionUID = -950545608880793468L;

  private String uuid;

  private long sizeInBytes;
  private long totalNumberOfFiles;

  private long numberOfDocumentationFiles;
  private long numberOfSchemaFiles;

  public IndexedRepresentation() {
    super();
  }

  public IndexedRepresentation(String uuid, String id, String aipId, boolean original, String type, long sizeInBytes,
    long totalNumberOfFiles, long numberOfDocumentationFiles, long numberOfSchemaFiles) {
    super(id, aipId, original, type);
    this.uuid = uuid;
    this.sizeInBytes = sizeInBytes;
    this.totalNumberOfFiles = totalNumberOfFiles;
    this.numberOfDocumentationFiles = numberOfDocumentationFiles;
    this.numberOfSchemaFiles = numberOfSchemaFiles;
  }

  @Override
  public String getUUID() {
    return uuid;
  }

  public void setUUID(String uuid) {
    this.uuid = uuid;
  }

  public long getSizeInBytes() {
    return sizeInBytes;
  }

  public void setSizeInBytes(long sizeInBytes) {
    this.sizeInBytes = sizeInBytes;
  }

  public long getTotalNumberOfFiles() {
    return totalNumberOfFiles;
  }

  public void setTotalNumberOfFiles(long totalNumberOfFiles) {
    this.totalNumberOfFiles = totalNumberOfFiles;
  }

  public long getNumberOfDocumentationFiles() {
    return numberOfDocumentationFiles;
  }

  public void setNumberOfDocumentationFiles(long numberOfDocumentationFiles) {
    this.numberOfDocumentationFiles = numberOfDocumentationFiles;
  }

  public long getNumberOfSchemaFiles() {
    return numberOfSchemaFiles;
  }

  public void setNumberOfSchemaFiles(long numberOfSchemaFiles) {
    this.numberOfSchemaFiles = numberOfSchemaFiles;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (numberOfDocumentationFiles ^ (numberOfDocumentationFiles >>> 32));
    result = prime * result + (int) (numberOfSchemaFiles ^ (numberOfSchemaFiles >>> 32));
    result = prime * result + (int) (sizeInBytes ^ (sizeInBytes >>> 32));
    result = prime * result + (int) (totalNumberOfFiles ^ (totalNumberOfFiles >>> 32));
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    IndexedRepresentation other = (IndexedRepresentation) obj;
    if (numberOfDocumentationFiles != other.numberOfDocumentationFiles)
      return false;
    if (numberOfSchemaFiles != other.numberOfSchemaFiles)
      return false;
    if (sizeInBytes != other.sizeInBytes)
      return false;
    if (totalNumberOfFiles != other.totalNumberOfFiles)
      return false;
    if (uuid == null) {
      if (other.uuid != null)
        return false;
    } else if (!uuid.equals(other.uuid))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "IndexedRepresentation [uuid=" + uuid + ", sizeInBytes=" + sizeInBytes + ", totalNumberOfFiles="
      + totalNumberOfFiles + ", numberOfDocumentationFiles=" + numberOfDocumentationFiles + ", numberOfSchemaFiles="
      + numberOfSchemaFiles + "]";
  }

}
