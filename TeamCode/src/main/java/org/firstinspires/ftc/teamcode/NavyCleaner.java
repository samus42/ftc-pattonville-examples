package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Navy Robot (Cleaner)")
public class NavyCleaner extends LinearOpMode {
    private final double minArmPosition = 0.91;
    private final double maxArmPosition = 0.5;

    private static class HardwareNames {
        public static final String ArmServo = "Arm Servo";
        public static final String ClawServo = "Claw Servo";
        public static final String FrontLeftWheel = "Front Left";
        public static final String RearLeftWheel = "Back Left";
        public static final String FrontRightWheel = "Front Right";
        public static final String RearRightWheel = "Rear Right";
        public static final String LeftSpool = "Left Spool";
        public static final String RightSpool = "right Spool";
    }

    private void initializeHardware() {
        Servo armServo = hardwareMap.servo.get(HardwareNames.ArmServo);
        DcMotor motorFrontLeft = hardwareMap.dcMotor.get(HardwareNames.FrontLeftWheel); //
        DcMotor motorBackLeft = hardwareMap.dcMotor.get(HardwareNames.RearLeftWheel);   //

        // Spool motors for rasing arm bar
        DcMotor leftSpool = hardwareMap.dcMotor.get(HardwareNames.LeftSpool);

        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotor.Direction.REVERSE);
        leftSpool.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Initial Arm Position", "(%.2f)", armServo.getPosition());
        telemetry.update();
    }

    private void adjustArmRotation() {
        // Because of the position of the rotor, a higher value is down, a lower value is up.
        final double moveDownIncrement = 0.001;
        final double moveUpIncrement = -0.001;
        Servo armServo = hardwareMap.servo.get(HardwareNames.ArmServo);
        double positionAdjustment = 0;

        if (gamepad1.left_bumper) {
            positionAdjustment = moveUpIncrement;
        } else if (gamepad1.right_bumper) {
            positionAdjustment = moveDownIncrement;
        }
        double newPosition = Range.clip(positionAdjustment + armServo.getPosition(), maxArmPosition, minArmPosition);

        if (gamepad1.right_bumper || gamepad1.left_bumper) {
            armServo.setPosition(newPosition);
        }
        telemetry.addData("Arm Position", "(%.2f)", armServo.getPosition());
    }

    private void adjustClaw() {
        final double clawClosedPosition = 0.0;
        final double clawOpenPosition = 0.5;

        Servo clawServo = hardwareMap.servo.get(HardwareNames.ClawServo);
        Servo armServo = hardwareMap.servo.get(HardwareNames.ArmServo);

        if (gamepad1.a) {
            clawServo.setPosition(clawOpenPosition);
        } else if (gamepad1.b && armServo.getPosition() < minArmPosition) {
            clawServo.setPosition(clawClosedPosition);
        }
        telemetry.addData("Claw Position", "(%.2f)", clawServo.getPosition());
    }

    private void adjustArmHeight() {
        DcMotor leftSpool = hardwareMap.dcMotor.get(HardwareNames.LeftSpool);
        DcMotor rightSpool = hardwareMap.dcMotor.get(HardwareNames.RightSpool);
        double spoolPower = 0;
        if (gamepad1.left_trigger != 0) {
            spoolPower = 0.35;
        } else if (gamepad1.right_trigger != 0) {
            spoolPower = -0.35;
        }
        leftSpool.setPower(spoolPower);
        rightSpool.setPower(spoolPower);
        telemetry.addData("Spool Power", "(%2.f)", spoolPower);
//            telemetry.addData("L Spool Position", String.valueOf(leftSpool.getCurrentPosition()));
//            telemetry.addData("R Spool Position", String.valueOf(rightSpool.getCurrentPosition()));
    }

    private void moveRobot() {
        // TODO: fix drift by tweaking some values

        DcMotor motorFrontRight = hardwareMap.dcMotor.get(HardwareNames.FrontRightWheel);
        DcMotor motorBackRight = hardwareMap.dcMotor.get(HardwareNames.RearRightWheel);
        DcMotor motorFrontLeft = hardwareMap.dcMotor.get(HardwareNames.FrontLeftWheel);
        DcMotor motorBackLeft = hardwareMap.dcMotor.get(HardwareNames.RearLeftWheel);

        double y = -gamepad1.left_stick_y; // Remember, this is reversed!
        double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;
        //we can fix controls eventually

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]
        // This is the math to get your rotating and driving and etc
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (Math.pow((y + x + rx), 3) * 1.0) / denominator;
        double backLeftPower = (Math.pow((y - x + rx), 3) * 1.0) / denominator;
        double frontRightPower = (Math.pow((y - x - rx), 3) * 1.0) / denominator;
        double backRightPower = (Math.pow((y + x - rx), 3) * 1.0) / denominator;

        motorFrontLeft.setPower(frontLeftPower);
        motorBackLeft.setPower(backLeftPower);
        motorFrontRight.setPower(frontRightPower);
        motorBackRight.setPower(backRightPower);

        telemetry.addData("Front Motors", "left (%.2f), right (%.2f)", frontLeftPower, frontRightPower);
        telemetry.addData("Rear Motors", "left (%.2f), right (%.2f)", backLeftPower, backRightPower);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initializeHardware();

        waitForStart();

        while (opModeIsActive()) {
            adjustArmRotation();
            adjustArmHeight();
            adjustClaw();
            moveRobot();

            telemetry.update();
        }
    }
}