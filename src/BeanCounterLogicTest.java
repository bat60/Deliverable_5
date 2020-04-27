import static org.junit.Assert.*;

import gov.nasa.jpf.vm.Verify;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>
 * Uses the Java Path Finder model checking tool to check BeanCounterLogic in
 * various modes of operation. It checks BeanCounterLogic in both "luck" and
 * "skill" modes for various numbers of slots and beans. It also goes down all
 * the possible random path taken by the beans during operation.
 */

public class BeanCounterLogicTest {
	private static BeanCounterLogic logic; // The core logic of the program
	private static Bean[] beans; // The beans in the machine
	private static String failString; // A descriptive fail string for assertions

	private static int slotCount; // The number of slots in the machine we want to test
	private static int beanCount; // The number of beans in the machine we want to test
	private static boolean isLuck; // Whether the machine we want to test is in "luck" or "skill" mode

	/**
	 * Sets up the test fixture.
	 */
	@BeforeClass
	public static void setUp() {
		/*
		 * TODO: Use the Java Path Finder Verify API to generate choices for slotCount,
		 * beanCount, and isLuck: slotCount should take values 1-5, beanCount should
		 * take values 0-3, and isLucky should be either true or false. For reference on
		 * how to use the Verify API, look at:
		 * https://github.com/javapathfinder/jpf-core/wiki/Verify-API-of-JPF
		 */

//		slotCount = Verify.getInt(1, 5);
		slotCount = Verify.getIntFromList(10);
//		slotCount = Verify.getInt(10, 10);
//		beanCount = Verify.getInt(0, 3);
		beanCount = Verify.getIntFromList(2);
		isLuck = Verify.getBoolean(false);

		// Create the internal logic
		logic = BeanCounterLogic.createInstance(slotCount);
		// Create the beans
		beans = new Bean[beanCount];
		for (int i = 0; i < beanCount; i++) {
			beans[i] = Bean.createInstance(slotCount, isLuck, new Random());
		}

		// A failstring useful to pass to assertions to get a more descriptive error.
		failString = "Failure in (slotCount=" + slotCount + ", "
				+ "beanCount=" + beanCount + ", isLucky=" + isLuck + "):";
	}

	@AfterClass
	public static void tearDown() {
	}

	/**
	 * Test case for void void reset(Bean[] beans). Preconditions: None. Execution
	 * steps: Call logic.reset(beans). Invariants: If beanCount is greater than 0,
	 * remaining bean count is beanCount - 1 in-flight bean count is 1 (the bean
	 * initially at the top) in-slot bean count is 0. If beanCount is 0, remaining
	 * bean count is 0 in-flight bean count is 0 in-slot bean count is 0.
	 */
	@Test
	public void testReset() {
		// TODO: Implement
		logic.reset(beans);
		int in_flight_beans = 0;
		int in_slot_beans = 0;
		for (int i = 0; i < slotCount; i++) {
			in_slot_beans += logic.getSlotBeanCount(i);
			if (logic.getInFlightBeanXPos(i) >= 0) {
				in_flight_beans += 1;
			}
		}
		if (beanCount > 0) {
			assertEquals(failString, (beanCount - 1), logic.getRemainingBeanCount());
			assertEquals(failString, 1, in_flight_beans);
			assertEquals(failString, 0, in_slot_beans);
		} else {
			assertEquals(failString, 0, logic.getRemainingBeanCount());
			assertEquals(failString, 0, in_flight_beans);
			assertEquals(failString, 0, in_slot_beans);
		}
		/*
		 * Currently, it just prints out the failString to demonstrate to you all the
		 * cases considered by Java Path Finder. If you called the Verify API correctly
		 * in setUp(), you should see all combinations of machines
		 * (slotCount/beanCount/isLucky) printed here:
		 * 
		 * Failure in (slotCount=1, beanCount=0, isLucky=false): Failure in
		 * (slotCount=1, beanCount=0, isLucky=true): Failure in (slotCount=1,
		 * beanCount=1, isLucky=false): Failure in (slotCount=1, beanCount=1,
		 * isLucky=true): ...
		 * 
		 * PLEASE REMOVE when you are done implementing.
		 */
		// System.out.println(failString);
	}

	/**
	 * Test case for boolean advanceStep(). Preconditions: None. Execution steps:
	 * Call logic.reset(beans). Call logic.advanceStep() in a loop until it returns
	 * false (the machine terminates). Invariants: After each advanceStep(), all
	 * positions of in-flight beans are legal positions in the logical coordinate
	 * system.
	 */
	@Test
	public void testAdvanceStepCoordinates() {
		// TODO: Implement
		logic.reset(beans);
		int i = 0;
		while (logic.advanceStep() == true && i < slotCount) {
			int xpos = logic.getInFlightBeanXPos(i);
			assertTrue(failString, (xpos <= 1 || xpos >= 0));
			i++;
		}
	}

	@Test
	public void testAdvanceStepBeanCount() {
		// TODO: Implement
		logic.reset(beans);
		while (logic.advanceStep() == true) {
			int in_flight_beans = 0;
			int in_slot_beans = 0;
			for (int i = 0; i < slotCount; i++) {
				in_slot_beans += logic.getSlotBeanCount(i);
				if (logic.getInFlightBeanXPos(i) >= 0) {
					in_flight_beans += 1;
				}
			}
			int remaining_beans = logic.getRemainingBeanCount();
			assertEquals(failString, beanCount, (remaining_beans + in_flight_beans + in_slot_beans));
		}
	}

	/**
	 * Test case for boolean advanceStep(). Preconditions: None. Execution steps:
	 * Call logic.reset(beans). Call logic.advanceStep() in a loop until it returns
	 * false (the machine terminates). Invariants: After the machine terminates,
	 * remaining bean count is 0 in-flight bean count is 0 in-slot bean count is
	 * beanCount.
	 */
	@Test
	public void testAdvanceStepPostCondition() {
		// TODO: Implement
		while (true) {
			if (logic.advanceStep() == false) {
				break; 
			}
		}
		int in_flight_beans = 0;
		int in_slot_beans = 0;
		for (int i = 0; i < slotCount; i++) {
			in_slot_beans += logic.getSlotBeanCount(i);
			if (logic.getInFlightBeanXPos(i) >= 0) {
				in_flight_beans += 1;
			}
		}
		int remaining_beans = logic.getRemainingBeanCount();
		assertEquals(failString, 0, remaining_beans);
		assertEquals(failString, 0, in_flight_beans);
		assertEquals(failString, beanCount, in_slot_beans);

	}

	/**
	 * Test case for void lowerHalf()(). Preconditions: None. Execution steps: Call
	 * logic.reset(beans). Call logic.advanceStep() in a loop until it returns false
	 * (the machine terminates). Call logic.lowerHalf(). Invariants: After calling
	 * logic.lowerHalf(), slots in the machine contain only the lower half of the
	 * original beans. Remember, if there were an odd number of beans, (N+1)/2 beans
	 * should remain. Check each slot for the expected number of beans after having
	 * called logic.lowerHalf().
	 */
	@Test
	public void testLowerHalf() {
//		// TODO: Implement
		logic.reset(beans);
		while (true) {
			if (logic.advanceStep() == false) {
				break; 
			}
		}
		
		int totalBeans = 0; 
		if (beanCount % 2 != 0 ) {
			totalBeans = (beanCount / 2) + 1; 
		} else {
			totalBeans = (beanCount / 2);
		}
		int[] bean_slots = new int[slotCount]; 
		for (int i = 0; i < slotCount; i++) {
			if (totalBeans < logic.getSlotBeanCount(i)) {
				bean_slots[i] = totalBeans;
				return;
			}
			bean_slots[i] = logic.getSlotBeanCount(i);
			totalBeans = totalBeans - logic.getSlotBeanCount(i);
		}
		logic.lowerHalf();
	    for (int i = 0; i < slotCount; i++) {
		      assertEquals(failString, bean_slots[i], logic.getSlotBeanCount(i));
	    }
	}

	/**
	 * Test case for void upperHalf(). Preconditions: None. Execution steps: Call
	 * logic.reset(beans). Call logic.advanceStep() in a loop until it returns false
	 * (the machine terminates). Call logic.lowerHalf(). Invariants: After calling
	 * logic.upperHalf(), slots in the machine contain only the upper half of the
	 * original beans. Remember, if there were an odd number of beans, (N+1)/2 beans
	 * should remain. Check each slot for the expected number of beans after having
	 * called logic.upperHalf().
	 */
	@Test
	public void testUpperHalf() {
		// TODO: Implement
		logic.reset(beans);
		while (true) {
			if (logic.advanceStep() == false) {
				break; 
			}
		}
		int totalBeans = 0; 
		if (beanCount % 2 != 0 ) {
			totalBeans = (beanCount / 2) + 1; 
		} else {
			totalBeans = (beanCount / 2);
		}
		int[] bean_slots = new int[slotCount]; 
		for (int i = slotCount - 1; i >= 0; i--) {
			if (totalBeans < logic.getSlotBeanCount(i)) {
				bean_slots[i] = totalBeans;
				return;
			}
			bean_slots[i] = logic.getSlotBeanCount(i);
			totalBeans = totalBeans - logic.getSlotBeanCount(i);
		}
		logic.lowerHalf();
	    for (int i = 0; i < slotCount; i++) {
		      assertEquals(failString, bean_slots[i], logic.getSlotBeanCount(i));
	    }
	}

	/**
	 * Test case for void repeat(). Preconditions: None. Execution steps: Call
	 * logic.reset(beans). Call logic.advanceStep() in a loop until it returns false
	 * (the machine terminates). Call logic.repeat(); Call logic.advanceStep() in a
	 * loop until it returns false (the machine terminates). Invariants: If the
	 * machine is operating in skill mode, bean count in each slot is identical
	 * after the first run and second run of the machine.
	 */
	@Test
	public void testRepeat() {
		// TODO: Implement
		// if not operating in skill mode, exit
		if (isLuck) {
			return;
		}
		logic.reset(beans);

		int[] beansInSlot = new int[slotCount];
		while (true) {
			if (logic.advanceStep() == false) {
				break;
			}
		}
		// first run
		for (int i = 0; i < beansInSlot.length; i++) {
			beansInSlot[i] = logic.getSlotBeanCount(i);
		}
		// repeat
		logic.repeat();
		while (true) {
			if (logic.advanceStep() == false) {
				break;
			}
		}
		// second run
		// bean count in each slot should be same

		for (int i = 0; i < beansInSlot.length; i++) {
			assertEquals(failString, beansInSlot[i], logic.getSlotBeanCount(i));
		}

	}

	/**
	 * Test case for double getAverageSlotBeanCount(). Preconditions: None.
	 * Execution steps: Call logic.reset(beans). Call 
	 * logic.advanceStep() in a loop until it returns false
	 * (the machine terminates). Call logic.getAverageSlotBeanCount(). Invariants: After 
	 * calling logic.getAverageSlotBeanCount(), the total number of beans should equal
	 * 
	 */

	@Test
	public void testGetAverageSlotBeanCount() {
		
	}

}
