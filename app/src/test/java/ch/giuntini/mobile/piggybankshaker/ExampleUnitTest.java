package ch.giuntini.mobile.piggybankshaker;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import ch.giuntini.mobile.piggybankshaker.service.DataManagerService;
import ch.giuntini.mobile.piggybankshaker.service.SlotMachineService;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private DataManagerService dataManagerService;
    private SlotMachineService slotMachineService;

    @Before
    public void init() {
        dataManagerService = new DataManagerService();
        slotMachineService = new SlotMachineService();
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void savingNumberOfCoinsAndBitcoins() {
        dataManagerService.incrementCoinsBy(48);
        dataManagerService.incrementBitcoinsBy(8.4775f);

        assertEquals(dataManagerService.getCoins(), 48);
        assertEquals(dataManagerService.getBitcoins(), 8.4775f, 0.01);
    }

    @Test
    public void randomSymbolSelection() {
        Symbol[] spin1 = slotMachineService.spin();
        Symbol[] spin2 = slotMachineService.spin();

        int cnt = 0;
        for (int i = 0; i < 4; i++) {
            if (spin1[i] == spin2[i]) {
                cnt++;
            }
        }

        // should the symbols be the same by any chance
        // we re-spin and check if they still are the same
        if (cnt == 3) {
            spin1 = slotMachineService.spin();
            spin2 = slotMachineService.spin();
            cnt = 0;
            for (int i = 0; i < 4; i++) {
                if (spin1[i] == spin2[i]) {
                    cnt++;
                }
            }
        }

        assertNotEquals(cnt, 3);
    }
}