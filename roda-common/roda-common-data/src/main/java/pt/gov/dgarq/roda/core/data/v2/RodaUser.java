package pt.gov.dgarq.roda.core.data.v2;

import java.util.HashSet;
import java.util.Set;

public class RodaUser extends RodaSimpleUser implements RODAMember {
	private static final long serialVersionUID = -718342371831706371L;

	private boolean active = true;
	private Set<String> allRoles = new HashSet<String>();
	private Set<String> directRoles = new HashSet<String>();
	private Set<String> allGroups = new HashSet<String>();
	private Set<String> directGroups = new HashSet<String>();

	public RodaUser() {
		super();
	}

	public RodaUser(String id, String name, String email, boolean guest) {
		super(id, name, email, guest);
	}

	public RodaUser(RodaSimpleUser user) {
		super(user.getId(), user.getName(), user.getEmail(), user.isGuest());
	}

	public RodaUser(String id, String name, String email, boolean guest, Set<String> allRoles, Set<String> directRoles,
			Set<String> allGroups, Set<String> directGroups) {
		super(id, name, email, guest);
		this.allRoles = allRoles;
		this.directRoles = directRoles;
		this.allGroups = allGroups;
		this.directGroups = directGroups;
	}

	public RodaUser(RodaSimpleUser user, Set<String> allRoles, Set<String> directRoles, Set<String> allGroups,
			Set<String> directGroups) {
		super(user.getId(), user.getName(), user.getEmail(), user.isGuest());
		this.allRoles = allRoles;
		this.directRoles = directRoles;
		this.allGroups = allGroups;
		this.directGroups = directGroups;
	}

	@Override
	public boolean isUser() {
		return true;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<String> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(Set<String> allRoles) {
		this.allRoles = allRoles;
	}

	public Set<String> getDirectRoles() {
		return directRoles;
	}

	public void setDirectRoles(Set<String> directRoles) {
		this.directRoles = directRoles;
	}

	public Set<String> getAllGroups() {
		return allGroups;
	}

	public void setAllGroups(Set<String> allGroups) {
		this.allGroups = allGroups;
	}

	public Set<String> getDirectGroups() {
		return directGroups;
	}

	public void setDirectGroups(Set<String> directGroups) {
		this.directGroups = directGroups;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((allGroups == null) ? 0 : allGroups.hashCode());
		result = prime * result + ((allRoles == null) ? 0 : allRoles.hashCode());
		result = prime * result + ((directGroups == null) ? 0 : directGroups.hashCode());
		result = prime * result + ((directRoles == null) ? 0 : directRoles.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RodaUser other = (RodaUser) obj;
		if (active != other.active)
			return false;
		if (allGroups == null) {
			if (other.allGroups != null)
				return false;
		} else if (!allGroups.equals(other.allGroups))
			return false;
		if (allRoles == null) {
			if (other.allRoles != null)
				return false;
		} else if (!allRoles.equals(other.allRoles))
			return false;
		if (directGroups == null) {
			if (other.directGroups != null)
				return false;
		} else if (!directGroups.equals(other.directGroups))
			return false;
		if (directRoles == null) {
			if (other.directRoles != null)
				return false;
		} else if (!directRoles.equals(other.directRoles))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RodaUser [").append(super.toString());
		builder.append(", active=").append(active);
		builder.append(", allRoles=").append(allRoles);
		builder.append(", directRoles=").append(directRoles);
		builder.append(", allGroups=").append(allGroups);
		builder.append(", directGroups=").append(directGroups);
		builder.append("]");
		return builder.toString();
	}

	public void addDirectRole(String role) {
		if(directRoles==null){
			directRoles = new HashSet<String>();
		}
		directRoles.add(role);
	}

	public void addGroup(String group) {
		if(allGroups==null){
			allGroups = new HashSet<String>();
		}
		allGroups.add(group);
	}

	public boolean isNameValid() {
		// TODO Auto-generated method stub
		return true;
	}
}
