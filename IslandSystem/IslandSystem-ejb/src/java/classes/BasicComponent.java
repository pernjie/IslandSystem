package classes;

import java.util.HashMap;
import java.util.Map;

public class BasicComponent {
    private long matno;
    private Map<Long,Integer> components;
    
    public BasicComponent() {
        components = new HashMap<Long,Integer>();
    }

    public long getMatno() {
        return matno;
    }

    public void setMatno(long matno) {
        this.matno = matno;
    }

    public Map<Long, Integer> getComponents() {
        return components;
    }

    public void setComponents(Map<Long, Integer> components) {
        this.components = components;
    }
    
}
