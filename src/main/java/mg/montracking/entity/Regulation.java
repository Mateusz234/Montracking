package mg.montracking.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Regulation {

	@Id
	@GeneratedValue
	private long id;
	@Column(name = "Bottom motor pwm")
	private int bottomMotorPwm;
	@Column(name = "Upper motor pwm")
	private int upperMotorPwm;
	@Column(name = "Error on X axis")
	private int xError;
	@Column(name = "Error on y axis")
	private int yError;

	public int getBottomMotorPwm() {
		return bottomMotorPwm;
	}

	public void setBottomMotorPwm(int bottomMotorPwm) {
		this.bottomMotorPwm = bottomMotorPwm;
	}

	public int getUpperMotorPwm() {
		return upperMotorPwm;
	}

	public void setUpperMotorPwm(int upperMotorPwm) {
		this.upperMotorPwm = upperMotorPwm;
	}

	public int getxError() {
		return xError;
	}

	public void setxError(int xError) {
		this.xError = xError;
	}

	public int getyError() {
		return yError;
	}

	public void setyError(int yError) {
		this.yError = yError;
	}

}
