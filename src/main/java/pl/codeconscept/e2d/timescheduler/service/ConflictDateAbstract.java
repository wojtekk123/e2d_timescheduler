package pl.codeconscept.e2d.timescheduler.service;


import java.util.Date;
import java.util.List;

public abstract class ConflictDateAbstract {

    abstract protected  <T> List<T> idConflict(Date dateFrom, Date dateTo);

    //dataStart form ride : 15:00:00
    //dateEnd form ride : 17:55:00
    //check if  the date of ride is between start and date

    protected boolean isWithinRange(Date singleRideDateStart, Date singleRideDateEnd, Date dateStart, Date dateEnd) {
        return (!(singleRideDateStart.before(dateStart) || singleRideDateStart.after(dateEnd)) || !(singleRideDateEnd.before(dateStart) || singleRideDateEnd.after(dateEnd)));
    }

}



