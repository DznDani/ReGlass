package restudio.reglass.extensions;

public interface PressableWidgetExtension {
    boolean reglass$isDragging();
    void reglass$setIsDragging(boolean isDragging);
    void reglass$beginPress(int button, boolean isDoubleClick);
    boolean reglass$canRelease(int button);
    boolean reglass$isDoubleClick();
}
