package view;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModelAndView {
    private String viewName;
    private Map<String, Object> model;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
        this.model = new HashMap<>();
    }
    public ModelAndView() {
        this.model = new HashMap<>();
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setAttribute(String key, Object value) {
        model.put(key, value);
    }
}