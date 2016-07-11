package com.popcorp.parser.skidkaonline.loader;

public class SalesCleaner {

    /*private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));

    private CityRepository cityRepository;

    private SaleRepository saleRepository;

    public void clearOldSales(){
        try {
            cityRepository = Application.getCityRepository();
            saleRepository = Application.getSaleRepository();
            for (City city : cityRepository.getAll()) {
                Calendar cityTime = Calendar.getInstance();
                cityTime.add(Calendar.HOUR_OF_DAY, city.getTimeZone());
                for (Sale sale : saleRepository.getAllForCityWithoutCommentsAndSames(city.getId())) {
                    Calendar saleTime = Calendar.getInstance();
                    try {
                        saleTime.setTime(format.parse(sale.getPeriodEnd()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        continue;
                    }
                    saleTime.add(Calendar.DAY_OF_YEAR, 1);
                    saleTime.set(Calendar.HOUR_OF_DAY, 0);
                    saleTime.set(Calendar.MINUTE, 30);
                    if (cityTime.getTimeInMillis() > saleTime.getTimeInMillis()) {
                        saleRepository.removeSale(city.getId(), sale.getId());
                    }
                }
            }
        } catch (Exception e){
            ErrorManager.sendError("Mestoskidki: Error clearning sales error: " + e.getMessage());
        }
    }*/
}
