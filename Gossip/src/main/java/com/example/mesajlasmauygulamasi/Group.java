package com.example.mesajlasmauygulamasi;

import java.util.List;

public class Group {
    private String groupName;
    private List<String> members;
    private String creator;

    public Group(String groupName, String creator, List<String> members) {
        this.groupName = groupName;
        this.creator = creator;
        this.members = members;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}