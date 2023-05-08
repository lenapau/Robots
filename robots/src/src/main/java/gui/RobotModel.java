package gui;

import javafx.util.Pair;

import java.awt.*;
import java.util.Observable;

import static gui.GameVisualizer.*;

public class RobotModel extends Observable
{
    private volatile double _positionX = 100;
    public double getPositionX()
    {
        return _positionX;
    }
    public void setPositionX(double positionX)
    {
        _positionX = positionX;
    }

    private volatile double _positionY = 100;
    public double getPositionY()
    {
        return _positionY;
    }
    public void setPositionY(double positionY)
    {
        _positionY = positionY;
    }

    private volatile double _direction = 0;
    public double getDirection()
    {
        return _direction;
    }
    public void setDirection(double direction)
    {
        _direction = direction;
    }

    public void move(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

        var newX = _positionX;
        var newY = _positionY;
        var newDirection = _direction;

        if (angularVelocity == 0)
        {
            newX = _positionX + velocity * duration * Math.cos(_direction);
            newY = _positionY + velocity * duration * Math.sin(_direction);
        }
        else
        {
            newDirection = asNormalizedRadians(_direction + angularVelocity * duration);
        }

        setPositionX(newX);
        setPositionY(newY);
        setDirection(newDirection);

        setChanged();
        notifyObservers(new Pair<>(newX, newY));
    }
}