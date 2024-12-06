import java.math.BigDecimal;

public class test {

    public static void main(String[] args) {
        String flume_type = "1.7976931348623157E308";
        System.out.println(new BigDecimal(flume_type));
        System.out.println(new BigDecimal("90").compareTo(   new BigDecimal(flume_type))        );
    }
}


