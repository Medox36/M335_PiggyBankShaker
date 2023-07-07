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
public class PiggyBankShakerUnitTest {

    private DataManagerService dataManagerService;
    private SlotMachineService slotMachineService;

    @Before
    public void init() {
        dataManagerService = new DataManagerService();
        slotMachineService = new SlotMachineService();
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

        int cnt = compare(spin1, spin2);

        // should the symbols be the same by any chance
        // we re-spin and check if they still are the same
        if (cnt == 3) {
            spin1 = slotMachineService.spin();
            spin2 = slotMachineService.spin();
            cnt = compare(spin1, spin2);
        }

        assertNotEquals(cnt, 3);
    }

    private int compare(Symbol[] symbols1, Symbol[] symbols2) {
        int cnt = 0;
        for (int i = 0; i < 3; i++) {
            if (symbols1[i] == symbols2[i]) {
                cnt++;
            }
        }
        return cnt;
    }
}