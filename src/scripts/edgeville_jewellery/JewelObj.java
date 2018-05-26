package scripts.edgeville_jewellery;

public class JewelObj {
    private final String name;
    private final int JEWEL;
    private final int BAR;
    private final int MOULD;
    private final int RESULT;

    public JewelObj(String name, int JEWEL, int BAR, int MOULD, int RESULT) {
        this.name = name;
        this.JEWEL = JEWEL;
        this.BAR = BAR;
        this.MOULD = MOULD;
        this.RESULT = RESULT;
    }

    /**
     * Gets name
     *
     * @return value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets JEWEL
     *
     * @return value of JEWEL
     */
    public int getJEWEL() {
        return JEWEL;
    }

    /**
     * Gets BAR
     *
     * @return value of BAR
     */
    public int getBAR() {
        return BAR;
    }

    /**
     * Gets MOULD
     *
     * @return value of MOULD
     */
    public int getMOULD() {
        return MOULD;
    }

    /**
     * Gets RESULT
     *
     * @return value of RESULT
     */
    public int getRESULT() {
        return RESULT;
    }
}
