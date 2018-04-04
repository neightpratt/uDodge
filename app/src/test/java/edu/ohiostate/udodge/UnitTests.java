package edu.ohiostate.udodge;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTests {
    @Test
    public void scoreTest() throws Exception {
        int testScore = 69;
        Score score = new Score();
        score.setScore(testScore);
        assertEquals(testScore, score.getScore());
    }
    @Test
    public void nameTest() throws Exception {
        String testName = "Jimmy John";
        Score score = new Score();
        score.setName(testName);
        assertEquals(testName, score.getName());
    }
    @Test
    public void uidTest() throws Exception {
        String Uid = "32";
        Score score = new Score();
        score.setUid(Uid);
        assertEquals(Uid, score.getUid());
    }
}