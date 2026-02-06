package ac4y.base.database;

import org.junit.Test;
import static org.junit.Assert.*;

public class Ac4yDBAdapterTest {

    @Test
    public void testAdapterInitialization() {
        Ac4yDBAdapter adapter = new Ac4yDBAdapter();
        assertNotNull(adapter);
    }
}
