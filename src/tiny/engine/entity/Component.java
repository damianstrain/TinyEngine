package tiny.engine.entity;

import java.awt.*;

/**
 * @author Damian Strain
 */
public interface Component {

    public void update();

    public void update(double deltaTime);

    public void render(Graphics2D g2d, float interpolation);

    public void render(Graphics2D g2d);
}
