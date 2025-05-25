package studit.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Profile implements Serializable {
    private final Set<String> interests;

    public Profile() {
        this.interests = new HashSet<>();
    }

    public void addInterest(String tag) {
        interests.add(tag.trim().toLowerCase());
    }

    public void removeInterest(String tag) {
        interests.remove(tag.trim().toLowerCase());
    }

    public boolean hasInterest(String tag) {
        return interests.contains(tag.trim().toLowerCase());
    }

    public Set<String> getInterests() {
        return interests;
    }

    @Override
    public String toString() {
        return "관심 태그: " + interests;
    }
}
