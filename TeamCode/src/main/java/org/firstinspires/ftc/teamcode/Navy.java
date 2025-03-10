package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Navy Robot")
public class Navy extends LinearOpMode {
    private final double minArmPosition = 0.91;
    private final double maxArmPosition = 0.5;
    private final double clawClosedPosition = 0.0;
    private final double clawOpenPosition = 0.5;
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        // TODO: change hardware IDs to match variable names
        // TODO: fix drift by tweaking some values
//        DcMotor mMotor = hardwareMap.dcMotor.get("somethingBetter"); // arm
//        Servo armServo = hardwareMap.servo.get("Arm Servo"); //claw
        Servo armServo = hardwareMap.get(Servo.class, "Arm Servo");
        Servo clawServo = hardwareMap.servo.get("Claw Servo"); //claw

        DcMotor motorFrontRight = hardwareMap.dcMotor.get("Front Right"); //
        DcMotor motorBackRight = hardwareMap.dcMotor.get("Back Right");   //
        DcMotor motorFrontLeft = hardwareMap.dcMotor.get("Front Left"); //
        DcMotor motorBackLeft = hardwareMap.dcMotor.get("Back Left");   //
        DcMotor leftSpool = hardwareMap.dcMotor.get("Left Spool");
        DcMotor rightSpool = hardwareMap.dcMotor.get("right Spool");
        int MIN_ARM_POSITION = -10;
        int MAX_ARM_POSITION = 325;
        int arm_rotation = -10;

        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotor.Direction.REVERSE);
        leftSpool.setDirection(DcMotorSimple.Direction.REVERSE);
//        armServo.setPosition(currentArmPosition);
        telemetry.addData("Initial Arm Position", "(%.2f)", armServo.getPosition());
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            double positionAdjustment = 0;
            double armPower = 0;
            int xo = 0;
            arm_rotation = 0;

            if (gamepad1.left_bumper) {
                positionAdjustment = -0.001;
//                positionAdjustment = 0.5;
            }
            if (gamepad1.right_bumper) {
                positionAdjustment = 0.001;
//                positionAdjustment = 0.91;
            }
            telemetry.addData("Pre Arm Adjust", "(%.2f)", armServo.getPosition());
//            telemetry.addData("Previous arm position", "(%.2f)", currentArmPosition);
//            currentArmPosition = Range.clip(positionAdjustment + currentArmPosition, maxArmPosition, minArmPosition);
//            telemetry.addData("Arm Adjustment", "(%.2f)", positionAdjustment);
//            telemetry.addData("Current Arm Position", "(%.2f)", currentArmPosition);
            double newPosition = Range.clip(positionAdjustment + armServo.getPosition(), maxArmPosition, minArmPosition);
            telemetry.addData("Calculated new value", "(%.2f)", newPosition);

            if (gamepad1.right_bumper || gamepad1.left_bumper) {
                armServo.setPosition(newPosition);
            }
            telemetry.addData("Arm Position", "(%.2f)", armServo.getPosition());

            if (gamepad1.a) {
                clawServo.setPosition(clawOpenPosition);
            } else if (gamepad1.b && armServo.getPosition() < minArmPosition) {
                clawServo.setPosition(clawClosedPosition);
            }
            telemetry.addData("Claw Position", "(%.2f)", clawServo.getPosition());

            double spoolPower = 0;
            if (gamepad1.left_trigger != 0) {
                spoolPower = 0.35;
            } else if (gamepad1.right_trigger != 0) {
                spoolPower = -0.35;
            }
            leftSpool.setPower(spoolPower);
            rightSpool.setPower(spoolPower);
//            telemetry.addData("Spool Power", "(%2.f)", leftSpool.getPower());
//            telemetry.addData("L Spool Position", String.valueOf(leftSpool.getCurrentPosition()));
//            telemetry.addData("R Spool Position", String.valueOf(rightSpool.getCurrentPosition()));
            telemetry.update();

//            mMotor.setPower(armPower);



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


        }
    }
}