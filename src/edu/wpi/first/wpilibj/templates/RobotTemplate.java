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

    Joystick joystick1 = new Joystick(1);             // Left Drive stick
    Joystick joystick2 = new Joystick(2);             // Right Drive stick
    Joystick shootstick = new Joystick(3);            // Shooter stick
    Talon shoot = new Talon(5);     //ALL VICTORS WILL BE TALONS ON THE BIG BOT
    Talon feed = new Talon(4);
    Victor lMotors = new Victor(1);     //Victors are other motor speed controller objects like Talons
    Victor rMotors = new Victor(3);
    Talon elevator = new Talon(2);      // Rotates worm screw to raise shooter mechanism
    RobotDrive chassis = new RobotDrive(lMotors, rMotors);      // Implements a predefined driving class using two motors
    Compressor compress = new Compressor(14, 1);
    Solenoid shifth = new Solenoid(1);              // Drive high gear shift
    Solenoid shiftl = new Solenoid(2);              // Drive low gear shift
    Solenoid shootout = new Solenoid(3);            // Piston extend
    Solenoid shootin = new Solenoid(4);             // Piston Retract
    Solenoid left1 = new Solenoid(5);               // Climbing mechanism left arms
    Solenoid left2 = new Solenoid(6);       
    Solenoid right1 = new Solenoid(7);              // Climbing mechanism right arm
    Solenoid right2 = new Solenoid(8);
    Timer shoottimer = new Timer();                 // Times shooting procedures
    Timer shifttimer = new Timer();                 // Times shifting procedures
    Timer pistontimer = new Timer();                // Times climbing procedures
    double voltage = DriverStation.getInstance().getBatteryVoltage();
    Encoder leftdrive = new Encoder(4, 5);          // Counts number of rotations from left drive
    Encoder rightdrive = new Encoder(6, 7);         // Counts number of rotations from right drive
    Encoder worm = new Encoder(8, 9);               // Counts the elevator worm screw rotations
    DigitalInput topswitch = new DigitalInput(1);   // Returns True if limit switch is pressed
    DigitalInput botswitch = new DigitalInput(2);   
    PIDController PIDencoder = new PIDController(.1, 0, 0, worm, elevator); // Uses first three arguements as PID values using
    float shooterOffset = 0;                                                // worm ecoder to get values while elevator motor
    int shootstate = -1; // Transitioning to different shooting states      // sets them
    boolean lastTimePress;                       //To check rising/falling edge of bumper switch
    long lastPrint = System.currentTimeMillis(); // initializes lastPrint with the current Systems time incase TimeMillis does not start at 0
    int switchstate = 0;                         // Ensures complete transitions from different states incase interupted
    double shooterspeed = .95;                   // Global shooter motor speed

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {                  // Autonomous code
        switchstate = 0;                        
        {
            while (switchstate == 0) {              // Autonomous State
                if (topswitch.get() == false) {     // Ensures shooter is raised before shooting
                    elevator.set(-1);               // Moves elevator up
                } else {
                    elevator.set(0);
                    
                    Timer.delay(.5);
                    switchstate = 1;             
                    shoot.set(-(shooterspeed));  // Shooting motors are set to rotate
                    feed.set(-(shooterspeed));
                    compress.start();           // Compressor starts up again until full if needed
                    
                    Timer.delay(4);             // Delays code from exceding past this point for four seconds
                }
            }

            if (switchstate == 1) {             // Autonomous Shooting State

                for (int i = 1; i <= 4; i++) {   // Local variable i starts at 1 and adds 1 after every cycle. Stops after 4.

                    shootout.set(true);
                    shootin.set(false);
                    Timer.delay(.07);           // A Timer.delay() is used here because no other functions are in use
                    shootout.set(false);        // while a shoottimer is needed when in teleop to prevent code from freezing
                    shootin.set(false);
                    Timer.delay(.07);
                    shootout.set(false);
                    shootin.set(true);
                    Timer.delay(.07);
                    shootout.set(false);
                    shootin.set(false);
                    
                    Timer.delay(1);

                }
                shoot.set(0);
                feed.set(0);
                switchstate = 2;
            }
        }
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        switchstate = 2;            // Teleop state
        compress.start();           
        worm.start();               // Encoders need start methods in order to begin counting rotations
        rightdrive.start();
        leftdrive.start();
        while (isEnabled() && isOperatorControl() && switchstate == 2) { //engages tankdrive
            chassis.tankDrive(joystick1, joystick2);

            //lMotors.set(joystick1.getY());
            //rMotors.set(joystick2.getY());

            if (System.currentTimeMillis() > lastPrint) {   // As time increases, prints every second
                System.out.println("==========================");
                System.out.println("Limit 1: " + topswitch.get());
                System.out.println("Limit 2: " + botswitch.get());
                System.out.println("worm89: " + worm.get());
                System.out.println("right67: " + rightdrive.get());
                System.out.println("left45: " + leftdrive.get());
                System.out.println("yvalue: " + shootstick.getY());       
               // System.out.println(DriverStationLCD.Line, int, StringBuffer);
               lastPrint += 1000;
            }



            if (joystick1.getTrigger()) {                   
                PIDencoder.setSetpoint(0 + shooterOffset);
            }


            if (lastTimePress == false && topswitch.get()) {
                shooterOffset = worm.get();
            }


            lastTimePress = topswitch.get();



            if (shootstick.getRawButton(8)) {
                elevator.set(shootstick.getY());    // Enables full control of motor binded to Y-axis of a Joystick
                
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

            if (shootstick.getRawButton(1)) {       // Starts up shooting motors
                
                    shoot.set(-(shooterspeed));
                    feed.set(-(shooterspeed));
                
            } else {
                shoot.set(0);
                feed.set(0);
            }
         
            
            if (shootstick.getRawButton(4)) {       // Arm pistons extend
                left2.set(true);
                right1.set(true);
                pistontimer.reset();                // Timers must reset to 0 and start again after every procedure
                pistontimer.start();
            }
            
            if (shootstick.getRawButton(5)) {     // Arm pistons retract
                left1.set(true);
                right2.set(true);
                pistontimer.reset();
                pistontimer.start();
            }
            
            if (pistontimer.get() > .3) {       // After .3 seconds solenoids are turned off to prevent unnecessary air
                left1.set(false);
                left2.set(false);
                right1.set(false);
                right2.set(false);
                pistontimer.reset();
                pistontimer.stop();
            }
            
            
            if (joystick1.getRawButton(1)) {    // Shifts drive to High Gear
                shifth.set(true);
                shiftl.set(false);
                shifttimer.reset();
                shifttimer.start();
            }

            if (joystick2.getRawButton(1)) {    // Shift drive to Low Gear
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


            if (shootstick.getRawButton(2) && shootstate == -1) {   // Begins shooting procedures. State prevents interuptions
                shootstate = 0;                                     // Same procedure as autonomous shots but uses shoottimer
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
    public void test() {        // Test was used only to start the compressor to fill the airtank
        switchstate = 3;
        if (switchstate == 3) {
            compress.start();
        } else {
            compress.stop();
        }

    }
}
