import java.util.Date;

/** Simple test of the getTime() kernel call.
 * CSIS421, Spring 2005.
 */
public class TimeTest {
	/** Main program.
	 * @param args ignored.
	 */
	public static void main(String[] args) {
		long now = Library.getTime();
		if (now < 0) {
			Library.output("Error: " + Library.errorMessage[(int) -now] + "\n");
		} else {
			Library.output(
                    "Current time is " + now
                    + " = " + new Date(now) + "\n");
		}
	} // main
} // TimeTest