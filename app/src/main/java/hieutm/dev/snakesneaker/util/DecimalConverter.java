package hieutm.dev.snakesneaker.util;

import java.text.DecimalFormat;

public class DecimalConverter {
    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.##");
        return formatter.format(Double.parseDouble(amount));
    }

    public static String saveFormat(String save) {
        String[] saves  = save.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < saves.length; i++) {
            if (i == 4) {
                i++;
            }
            if (i == 5) {
                builder.append(DecimalConverter.currencyFormat(saves[i])).append("đ ");
                i++;
            }
            builder.append(saves[i]).append(" ");
        }
        return builder.toString();
    }
}
