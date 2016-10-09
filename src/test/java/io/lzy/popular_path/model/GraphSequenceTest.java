package io.lzy.popular_path.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import io.lzy.popular_path.LogParser;
import io.lzy.popular_path.TestBase;

import static org.testng.Assert.*;

/**
 * @author zhiyan
 */
public class GraphSequenceTest extends TestBase {
    @Test
    public void testGraphSequenceWithMultipleUsersAndCrossAccess() {
        StringBuffer testLog = new StringBuffer();
        testLog.append("U1\t/\n");
        testLog.append("U1\tN1\n");
        testLog.append("U2\t/\n");
        testLog.append("U2\tN1\n");
        testLog.append("U1\tN2\n");
        testLog.append("U1\tN3\n");
        testLog.append("U2\tN2\n");
        testLog.append("U2\tN3\n");
        testLog.append("U3\t/\n");
        testLog.append("U3\tN4\n");
        testLog.append("U3\tN5\n");

        InputStream stream = new ByteArrayInputStream(testLog.toString().getBytes());
        GraphSequence graph = new GraphSequence();
        try {
            LogParser.parseLog(stream, graph);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        Map<String, List<Map.Entry<String, Integer>>> ret1;
        ret1 = graph.getPopularPath(TOP(5));
        // {U1=[/N1/N2=3, N1/N2/N3=3], U2=[N1/N2/N3=3, /N1/N2=3], U3=[/N4/N5=3]}
        assertEquals(ret1.keySet().size(), 3);
        List<String> edgeOwners = new ArrayList<>(ret1.keySet());
        Collections.sort(edgeOwners);
        assertEquals(edgeOwners.size(), 3);
        assertEquals(edgeOwners.get(0), TEST_USER_1);
        assertEquals(edgeOwners.get(1), TEST_USER_2);
        assertEquals(edgeOwners.get(2), TEST_USER_3);
        ret1.get(TEST_USER_1).sort(comparator);
        assertEquals(ret1.get(TEST_USER_1).size(), 2);
        assertEquals(ret1.get(TEST_USER_1).get(0).getKey(), "/N1/N2");
        assertEquals(ret1.get(TEST_USER_1).get(0).getValue(), new Integer(3));
        assertEquals(ret1.get(TEST_USER_1).get(1).getKey(), "N1/N2/N3");
        assertEquals(ret1.get(TEST_USER_1).get(1).getValue(), new Integer(3));
        ret1.get(TEST_USER_2).sort(comparator);
        assertEquals(ret1.get(TEST_USER_2).size(), 2);
        assertEquals(ret1.get(TEST_USER_2).get(0).getKey(), "/N1/N2");
        assertEquals(ret1.get(TEST_USER_2).get(0).getValue(), new Integer(3));
        assertEquals(ret1.get(TEST_USER_2).get(1).getKey(), "N1/N2/N3");
        assertEquals(ret1.get(TEST_USER_2).get(1).getValue(), new Integer(3));
        ret1.get(TEST_USER_3).sort(comparator);
        assertEquals(ret1.get(TEST_USER_3).size(), 1);
        assertEquals(ret1.get(TEST_USER_3).get(0).getKey(), "/N4/N5");
        assertEquals(ret1.get(TEST_USER_3).get(0).getValue(), new Integer(3));

        List<Map.Entry<String, Integer>> ret2;

        ret2 = graph.getPopularPath(TOP(3), TEST_USER_2);   // [/N1/N2=3, N1/N2/N3=3]
        assertEquals(ret2.size(), 2);
        ret2.sort(comparator);
        assertEquals(ret2.get(0).getKey(), "/N1/N2");
        assertEquals(ret2.get(0).getValue(), new Integer(3));
        assertEquals(ret2.get(1).getKey(), "N1/N2/N3");
        assertEquals(ret2.get(1).getValue(), new Integer(3));

        ret2 = graph.getPopularPath(TOP(3), TEST_USER_3);   // [/N4/N5=3]
        assertEquals(ret2.size(), 1);
        ret2.sort(comparator);
        assertEquals(ret2.get(0).getKey(), "/N4/N5");
        assertEquals(ret2.get(0).getValue(), new Integer(3));

        stream = new ByteArrayInputStream(testLog.toString().getBytes());
        graph = new GraphSequence(DEPTH(4));
        try {
            LogParser.parseLog(stream, graph);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        ret2 = graph.getPopularPath(TOP(1), TEST_USER_3);   // []
        assertEquals(ret2.size(), 0);
    }
}
