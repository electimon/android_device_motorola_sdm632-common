package com.moto.actions;

public class VibrationPresets {

    public static final String[] one = {"9", "9", "9", "9", "9"};
    public static final String[] two = {"60", "15", "30", "138", "100"};
    public static final String[] three = {"1", "1", "1", "1", "56"};

    enum Presets {
        DEFAULT, SOFT, BALANCED, HARD, GABISPECIAL;
        public Presets toEnum(int index) {
            switch (index) {
                case 0:
                    return DEFAULT;
                case 2:
                    return SOFT;
                case 3:
                    return BALANCED;
                case 4:
                    return HARD;
                case 5:
                    return GABISPECIAL;
            }
            return null;
        }
    }

    public void setValue(String value) {
	VibrationCalibration cali = new VibrationCalibration();

        int index = Integer.parseInt(value);
        cali.setValueABC(one[index], two[index], three[index]);
    }
}
