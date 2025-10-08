package com.dyaco.spirit_commercial.egym;

public class SystemContext {
    private SystemStrategy systemStrategy;

    public SystemContext(String userVersion) {
        // 根据用户版本初始化对应策略
        switch (userVersion) {
            case "egym":
                systemStrategy = new SystemEgymStrategy();
                break;
            case "spirit":
                systemStrategy = new SystemSpiritStrategy();
                break;
            default:
                throw new IllegalArgumentException("Unknown version: " + userVersion);
        }
    }

    public SystemContext(SystemStrategy systemStrategy) {
        this.systemStrategy = systemStrategy;
    }

    public void setSystemStrategy(SystemStrategy systemStrategy) {
        this.systemStrategy = systemStrategy;
    }

    public void executeLogic() {
        systemStrategy.setupLogic();
    }

    public void executeUI() {
        systemStrategy.setupUI();
    }
}