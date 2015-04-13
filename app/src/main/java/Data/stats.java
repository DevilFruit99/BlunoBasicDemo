package Data;

/**
 * Created by DevilFruit on 4/12/2015.
 */
public class stats {

    private double temp;
    private double CO;
    private double Smoke;
    //private String time;

    public stats(double temp, double CO, double Smoke) {
        this.temp = temp;
        this.CO = CO;
        this.Smoke = Smoke;
        //Calendar c = Calendar.getInstance();
        //SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        //time = df.format(c.getTime());
//
    }
    //public Time getStatTime() {
    //    return statTime;
    //}

    public double getCO() {
        return CO;
    }

    public void setCO(double CO) {
        this.CO = CO;
    }

    public double getSmoke() {
        return Smoke;
    }

    //public void setStatTime(Time statTime) {
    //    this.statTime = statTime;
    //}

    public void setSmoke(double smoke) {
        Smoke = smoke;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        //return (statTime.toString()+", "+temp+"F, "+" "+CO+", "+Smoke);
        //return (time+",   "+temp+"F,           "+CO+",          "+Smoke+"\n");
        return (temp + "F,           " + CO + ",          " + Smoke + "\n");
    }
}
