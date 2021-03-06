import gov.nasa.jpf.annotation.FilterField;

import java.util.Random;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>
 * Bean: Each bean is assigned a skill level from 0-9 on creation according to a
 * normal distribution with average SKILL_AVERAGE and standard deviation
 * SKILL_STDEV. The formula to calculate the skill level is:
 * 
 * <p>
 * SKILL_AVERAGE = (double) SLOT_COUNT * 0.5 SKILL_STDEV = (double)
 * Math.sqrt(SLOT_COUNT * 0.5 * (1 - 0.5)) SKILL_LEVEL = (int)
 * Math.round(rand.nextGaussian() * SKILL_STDEV + SKILL_AVERAGE)
 * 
 * <p>
 * A skill level of 9 means it always makes the "right" choices (pun intended)
 * when the machine is operating in skill mode ("skill" passed on command line).
 * That means the bean will always go right when a peg is encountered, resulting
 * it falling into slot 9. A skill level of 0 means that the bean will always go
 * left, resulting it falling into slot 0. For the in-between skill levels, the
 * bean will first go right then left. For example, for a skill level of 7, the
 * bean will go right 7 times then go left twice.
 * 
 * <p>
 * Skill levels are irrelevant when the machine operates in luck mode. In that
 * case, the bean will have a 50/50 chance of going right or left, regardless of
 * skill level. The formula to calculate the direction is: rand.nextInt(2). If
 * the return value is 0, the bean goes left. If the return value is 1, the bean
 * goes right.
 */

public class BeanImpl implements Bean {
	// TODO: Add member methods and variables as needed
	private boolean isLuck;
	private Random rand;
	private double skill_average;
	private double skill_stdev;
	private int skill_level;
	private int between_skill_levels;
	private int direction;

	/**
	 * Constructor - creates a bean in either luck mode or skill mode.
	 * 
	 * @param slotCount
	 *            the number of slots in the machine
	 * @param isLuck
	 *            whether the bean is in luck mode
	 * @param rand
	 *            the random number generator
	 */
	BeanImpl(int slotCount, boolean isLuck, Random rand) {
		// TODO: Implement
		this.isLuck = isLuck;
		this.rand = rand;
		this.direction = 0;
		skill_average = (double) slotCount * 0.5;
		skill_stdev = (double) Math.sqrt(slotCount * 0.5 * (1 - 0.5));
		skill_level = (int) Math.round(rand.nextGaussian() * skill_stdev + skill_average);
//		skill_level = 3
		//initialize 
	    between_skill_levels = skill_level;
	}

	// setter, getter
	public void setDirection(int dir) {
		this.direction = dir;
	}
	
	public int getDirection() {
		return this.direction;
	}

	public void setSkill() {
		between_skill_levels = skill_level;
	}
	
	public int getSkill() {
		return between_skill_levels;
	}
	
	/**
	 * Formula for choosing which direction to travel based on luck or not or skill_level
	 * Go right if rand.nextInt(2) == 1
	 * */
	public void whichDirection() {
		if (isLuck) {
			if (rand.nextInt(2) == 1) {
				direction++;
			}
		} else if (between_skill_levels > 0) {
			direction++;
			between_skill_levels--;
		}		
	}	
}
