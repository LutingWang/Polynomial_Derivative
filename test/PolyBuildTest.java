import org.junit.Test;
import JUnitTestTools.EnhancedUserTestTools;
import poly.Poly;

import java.io.File;

public class PolyBuildTest {
    
    @Test
    public void main() throws Exception {
        new EnhancedUserTestTools(PolyBuild.class, 2000).testAll(new File("./test/poly/test1.txt"));
    }
    
    @Test
    public void output() throws Exception {
        new EnhancedUserTestTools(PolyBuild.class, 2000)
                .testAll(new File("./test/case_nest.txt"), new File("./test/out_nest.txt"));
    }
    
    @Test
    public void t1() {
        String s = "cos((sin(x)^-1*cos(x)))";
        Poly poly = new PolyBuild(s).parsePoly();
        System.out.println(poly.differentiate());
    }
    
    @Test
    public void parsePolyTest() {
    }
}