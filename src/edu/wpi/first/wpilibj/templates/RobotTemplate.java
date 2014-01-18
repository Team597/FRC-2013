/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
//import edu.wpi.first.wpilibj.DriverStationLCD;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 *
 * THIS IS THE CODE
 */
public class RobotTemplate extends SimpleRobot {

    Joystick joystick1 = new Joystick(1);             //Left stick
    Joystick joystick2 = new Joystick(2);             //Right stick
    Joystick shootstick = new Joystick(3);            //Shooter stick
    Talon shoot = new Talon(5);     //ALL VICTORS WILL BE TALONS ON THE BIG BOT
    Talon feed = new Talon(4);
    Victor lMotors = new Victor(1);
    Victor rMotors = new Victor(3);
    Talon elevator = new Talon(2);
    RobotDrive chassis = new RobotDrive(lMotors, rMotors);
    Compressor compress = new Compressor(14, 1);
    Solenoid shifth = new Solenoid(1);
    Solenoid shiftl = new Solenoid(2);
    Solenoid shootout = new Solenoid(3);
    Solenoid shootin = new Solenoid(4);
    Solenoid left1 = new Solenoid(5);
    Solenoid left2 = new Solenoid(6);
    Solenoid right1 = new Solenoid(7);
    Solenoid right2 = new Solenoid(8);
    Timer shoottimer = new Timer();
    Timer shifttimer = new Timer();
    Timer pistontimer = new Timer();
    double voltage = DriverStation.getInstance().getBatteryVoltage();
    Encoder leftdrive = new Encoder(4, 5);
    Encoder rightdrive = new Encoder(6, 7);
    Encoder worm = new Encoder(8, 9);
    DigitalInput topswitch = new DigitalInput(1);
    DigitalInput botswitch = new DigitalInput(2);
    PIDController PIDencoder = new PIDController(.1, 0, 0, worm, elevator);
    float shooterOffset = 0;
    int shootstate = -1;
    boolean lastTimePress; //To check rising/falling edge of bumper switch
    long lastPrint = 0;
    int switchstate = 0;
    double shooterspeed = .95;

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        switchstate = 0;
        {
            while (switchstate == 0) {
                if (topswitch.get() == false) {
                    elevator.set(-1);
                } else {
                    elevator.set(0);
                    
                    Timer.delay(.5);
                    switchstate = 1;
                    shoot.set(-(shooterspeed));
                    feed.set(-(shooterspeed));
                    compress.start();
                    
                    Timer.delay(4);
                }
            }

            if (switchstate == 1) {

                for (int i = 1; i <= 4; i++) {

                   

                    shootout.set(true);
                    shootin.set(false);
                    Timer.delay(.07);
                    shootout.set(false);
                    shootin.set(false);
                    Timer.delay(.07);
                    shootout.set(false);
                    shootin.set(true);
                    Timer.delay(.07);
                    shootout.set(false);
                    shootin.set(false);
                    
                    Timer.delay(1);

                }
                switchstate = 2;
            }
            shoot.set(0);
            feed.set(0);
        }
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        switchstate = 2;
        compress.start();
        worm.start();
        rightdrive.start();
        leftdrive.start();
        while (isEnabled() && isOperatorControl() && switchstate == 2) { //engages tankdrive
            chassis.tankDrive(joystick1, joystick2);

            //lMotors.set(joystick1.getY());
            //rMotors.set(joystick2.getY());

            if (System.currentTimeMillis() > lastPrint + 500) {
                System.out.println("==========================");
                System.out.println("Limit 1: " + topswitch.get());
                System.out.println("Limit 2: " + botswitch.get());
                System.out.println("worm89: " + worm.get());
                System.out.println("right67: " + rightdrive.get());
                System.out.println("left45: " + leftdrive.get());
                System.out.println("yvalue: " + shootstick.getY());       
               // System.out.println(DriverStationLCD.Line, int, StringBuffer);
            }



            if (joystick1.getTrigger()) {
                PIDencoder.setSetpoint(0 + shooterOffset);
            }


            if (lastTimePress == false && topswitch.get()) {
                shooterOffset = worm.get();
            }


            lastTimePress = topswitch.get();



            if (shootstick.getRawButton(8)) {
                elevator.set(shootstick.getY());
                
                if (topswitch.get() == true && shootstick.getY() < 0) {
                    elevator.set(0);
                } else if (botswitch.get() == true && shootstick.getY() > 0) {
                    elevator.set(0);
                } else {
                    elevator.set(shootstick.getY());
                }
            } else {
                elevator.set(0);
            }

            if (shootstick.getRawButton(1)) {
                
                    shoot.set(-(shooterspeed));
                    feed.set(-(shooterspeed));
                
            } else {
                shoot.set(0);
                feed.set(0);
            }
         
            
            if (shootstick.getRawButton(4)) {
                left2.set(true);
                right1.set(true);
                pistontimer.reset();
                pistontimer.start();
            }
            
            if (shootstick.getRawButton(5)) {
                left1.set(true);
                right2.set(true);
                pistontimer.reset();
                pistontimer.start();
            }
            
            if (pistontimer.get() > .3) {
                left1.set(false);
                left2.set(false);
                right1.set(false);
                right2.set(false);
                pistontimer.reset();
                pistontimer.stop();
            }
            
            
            if (joystick1.getRawButton(1)) {
                shifth.set(true);
                shiftl.set(false);
                shifttimer.reset();
                shifttimer.start();
            }

            if (joystick2.getRawButton(1)) {
                shifth.set(false);
                shiftl.set(true);
                shifttimer.reset();
                shifttimer.start();
            }
            
            

            if (shifttimer.get() > .3) {
                shifth.set(false);
                shiftl.set(false);
                shifttimer.reset();
                shifttimer.stop();
            }


            if (shootstick.getRawButton(2) && shootstate == -1) {
                shootstate = 0;
                shoottimer.start();
                shootout.set(true);
                shootin.set(false);
            }
            if (shoottimer.get() > .05 && shootstate == 0) {
                shootstate = 1;
                shootout.set(false);
                shootin.set(false);
            }
            if (shoottimer.get() > .1 && shootstate == 1) {
                shootout.set(false);
                shootin.set(true);
                shootstate = 2;
            }
            if (shoottimer.get() > .5 && shootstate == 2) {
                shootout.set(false);
                shootin.set(false);
                shoottimer.stop();
                shoottimer.reset();
                shootstate = -1;

            }

        }
    }

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {
        switchstate = 3;
        if (switchstate == 3) {
            compress.start();
        } else {
            compress.stop();
        }

    }
}
