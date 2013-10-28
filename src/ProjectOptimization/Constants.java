package ProjectOptimization;

public class Constants
{
    //Average US electricity Cost as of August 2013
    //Sales, Revenue, and Average Retail Price for August Table:
    //http://www.eia.gov/electricity/monthly/pdf/epm.pdf
    
    //conversion factor based off of:
    //http://www.epa.gov/cleanenergy/documents/egridzips/eGRID2012V1_0_year09_GHGOutputrates.pdf
    
    //conversion factor for gas based off of:
    //http://www.epa.gov/cleanenergy/energy-resources/refs.html (Therms of Natural Gas section) 
    
    //1 J = 2.77777778e-10 MWH
    //1 J = 9.48043428e-9 Therms

    public static final double MWH_CONVERSION = Double.parseDouble("2.77777778e-10");
    public static final double THERM_CONVERSION = Double.parseDouble("9.48043428e-9");
    public static final double US_AVG_CO2_LBS_PER_MWH = 1216.18;
    public static final double US_AVG_COST_DOLLARS_PER_KWH = .1251;
    public static final double KG_PER_LB = 0.453592;
    public static final double METRIC_TONS_CO2_PER_THERM = 0.005306;
}
