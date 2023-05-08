package gui;

import java.awt.*;

public class RobotController
{
    public RobotController (RobotModel robotModel, RobotView robotView)
    {
        this.robotModel = robotModel;
        this.robotView = robotView;
    }

    private final RobotModel robotModel;
    private final RobotView robotView;

    public double getPositionX()
    {
        return robotModel.getPositionX();
    }

    public double getPositionY()
    {
        return robotModel.getPositionY();
    }

    public double getDirection()
    {
        return robotModel.getDirection();
    }

    public void move(double velocity, double angularVelocity, double duration)
    {
        robotModel.move(velocity, angularVelocity, duration);
    }

    public void draw(Graphics2D g)
    {
        robotView.draw(g, robotModel);
    }

}