package com.group24h.enlistment;

import java.util.Collection;
import java.util.HashSet;

import static org.apache.commons.lang3.Validate.isTrue;

public class Subject {
    private final String subjectId;
    private Collection<Subject> prerequisites = new HashSet<>();

    public Subject(String subjectId, Collection<Subject> prerequisites) {
        this.subjectId = subjectId;
        this.prerequisites = prerequisites;
    }

    void checkPrerequisites(Collection<Subject> completedSubjects) {
        prerequisites.forEach(currPrerequisite ->
                isTrue(completedSubjects.contains(currPrerequisite), "missing prerequisite: " + currPrerequisite));
    }

}
