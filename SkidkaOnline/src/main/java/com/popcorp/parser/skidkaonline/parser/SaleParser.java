package com.popcorp.parser.skidkaonline.parser;

public class SaleParser {

    /*public Observable<Sale> getSale(int cityId, int saleId, CategoryInnerRepository categoryInnerRepository) {
        return APIFactory.getAPI().getSale(cityId, saleId)
                .flatMap(responseBody -> {
                    try {
                        String page;
                        try {
                            page = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            ErrorManager.sendError("Mestoskidki: Page with sale not loaded! Id: " + saleId + ", cityId: " + cityId + ", error: " + e.getMessage());
                            return Observable.empty();
                        }
                        String title = getTitle(page);
                        if (title == null) {
                            ErrorManager.sendError("Mestoskidki: Title for sale not finded! Id: " + saleId + ", cityId: " + cityId);
                            return Observable.empty();
                        }
                        String subTitle = getSubTitle(page);

                        String periodStart = getPeriodStart(page);
                        if (periodStart == null) {
                            ErrorManager.sendError("Mestoskidki: PeriodStart for sale not finded! Id: " + saleId + ", cityId: " + cityId);
                            return Observable.empty();
                        }

                        String periodEnd = getPeriodEnd(page);
                        periodEnd = periodEnd == null ? periodStart : periodEnd;

                        String coast = getCoast(page);
                        if (coast.isEmpty()) {
                            ErrorManager.sendError("Mestoskidki: Coast for sale not finded! Id: " + saleId + ", cityId: " + cityId);
                        }

                        String quantity = getQuantity(page);
                        String coastForQuantity = getQuantityCoast(page);

                        int shopId = getShopId(page);
                        if (shopId == -1) {
                            ErrorManager.sendError("Mestoskidki: Shop for sale not finded! Id: " + saleId + ", cityId: " + cityId);
                            return Observable.empty();
                        }

                        int categoryId = categoryInnerRepository.getCategoryForInner(getCategoryId(page));

                        int categoryType = getCategoryType(page);
                        if (categoryType == -1) {
                            ErrorManager.sendError("Mestoskidki: CategoryType for sale not finded! Id: " + saleId + ", cityId: " + cityId);
                            return Observable.empty();
                        }

                        String image = getImage(page);
                        if (image.isEmpty()) {
                            ErrorManager.sendError("Mestoskidki: Image for sale not finded! Id: " + saleId + ", cityId: " + cityId);
                            return Observable.empty();
                        }

                        Sale sale = new Sale(saleId, title, subTitle, periodStart, periodEnd, coast, quantity, coastForQuantity, image, cityId, shopId, categoryId, categoryType);
                        sale.setComments(getComments(page, cityId, sale.getId()));
                        sale.setSameSales(getSameSales(page, cityId, saleId));
                        return Observable.just(sale);
                    } catch (Exception e){
                        ErrorManager.sendError(e.getMessage());
                    }
                    return null;
                });
    }

    private String getImage(String page){
        String result = "";
        Matcher imageMatcher = Pattern.compile("class='img_big[0-9]*' align='left' src='[.[^']]*").matcher(page);
        if (imageMatcher.find()){
            String imageResult = imageMatcher.group();
            result = imageResult.substring(34);
        }
        return result;
    }

    private Iterable<SaleSame> getSameSales(String page, int cityId, int saleId) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));
        ArrayList<SaleSame> result = new ArrayList<>();
        Matcher sameMatcher = Pattern.compile("<a href='view_sale\\.php\\?city=[0-9]*&id=[0-9]*' title='[.[^']]*' class='same_sales'>[.[^<]]*</a></p></td><td align='[a-z]*' width='[0-9]*' valign='[a-z]*'><p class='same_sales'>[0-9\\.]* руб\\.</p></td><td align='[a-z]*' width='[0-9]*' valign='[a-z]*'><p class='same_sales'>[.[^<]]*</p></td><td align='[a-z]*' width='[0-9]*' valign='[a-z]*'><p class='same_sales'>(с|c) [\\.0-9]*</p></td><td align='[a-z]*' width='[0-9]*' valign='[a-z]*'><p class='same_sales'>по [\\.0-9]*</p>").matcher(page);
        while (sameMatcher.find()){
            String sameResult = sameMatcher.group();
            int id;
            String text;
            String coast;
            String shopName;
            String periodBegin;
            String periodEnd;
            Matcher idMatcher = Pattern.compile("&id=[0-9]*").matcher(sameResult);
            if (idMatcher.find()){
                String idResult = idMatcher.group();
                id = Integer.valueOf(idResult.substring(4));
            } else{
                ErrorManager.sendError("Mestoskidki: SaleSame id for sale not finded! Id: " + saleId + ", cityId: " + cityId + ", same: " + sameResult);
                continue;
            }
            Matcher textMatcher = Pattern.compile("title='[.[^']]*").matcher(sameResult);
            if (textMatcher.find()){
                String textResult = textMatcher.group();
                text = textResult.substring(7);
            } else{
                ErrorManager.sendError("Mestoskidki: SaleSame text for sale not finded! Id: " + saleId + ", cityId: " + cityId + ", same: " + sameResult);
                continue;
            }
            Matcher coastMatcher = Pattern.compile("[0-9\\.]* руб\\.").matcher(sameResult);
            if (coastMatcher.find()){
                String coastResult = coastMatcher.group();
                coast = coastResult;
            } else{
                ErrorManager.sendError("Mestoskidki: SaleSame coast for sale not finded! Id: " + saleId + ", cityId: " + cityId + ", same: " + sameResult);
                continue;
            }
            Matcher shopMatcher = Pattern.compile("<p class='same_sales'>[.[^<]]*</p>").matcher(sameResult);
            if (shopMatcher.find()){
                shopMatcher.find();//Первым находится цена, поэтому ищем следующий результат
                String shopResult = shopMatcher.group();
                shopName = shopResult.substring(22, shopResult.length() - 4);
            } else{
                ErrorManager.sendError("Mestoskidki: SaleSame shop for sale not finded! Id: " + saleId + ", cityId: " + cityId + ", same: " + sameResult);
                continue;
            }
            Matcher periodStartMatcher = Pattern.compile("(с|c) [\\.0-9]*</p>").matcher(sameResult);
            if (periodStartMatcher.find()){
                String periodStartResult = periodStartMatcher.group();
                periodBegin = periodStartResult.substring(2, periodStartResult.length() - 4);
            } else{
                ErrorManager.sendError("Mestoskidki: SaleSame periodStart for sale not finded! Id: " + saleId + ", cityId: " + cityId + ", same: " + sameResult);
                continue;
            }
            Matcher periodEndMatcher = Pattern.compile("по [\\.0-9]*</p>").matcher(sameResult);
            if (periodEndMatcher.find()){
                String perioEndResult = periodEndMatcher.group();
                periodEnd = perioEndResult.substring(3, perioEndResult.length() - 4);
            } else{
                ErrorManager.sendError("Mestoskidki: SaleSame periodEnd for sale not finded! Id: " + saleId + ", cityId: " + cityId + ", same: " + sameResult);
                continue;
            }
            Calendar today = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            try {
                end.setTime(format.parse(periodEnd));
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }
            if (end.getTimeInMillis() < today.getTimeInMillis()){
                continue;
            }
            SaleSame saleSame = new SaleSame(saleId, cityId, id, text, coast, shopName, periodBegin, periodEnd);
            result.add(saleSame);
        }
        return result;
    }

    private Iterable<SaleComment> getComments(String page, int cityId, int saleId) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("ru"));
        ArrayList<SaleComment> result = new ArrayList<>();
        Matcher commentMatcher = Pattern.compile("<p class='comment_add'><b>[.[^<]]*</b>(  для  [.[^<]]*</b>)? <br></p>[.[^<]]*<p class='comment_text'>[.[^<]]*</p><p class='comment_add'>[.0-9:[^<]]*</p>").matcher(page);
        while (commentMatcher.find()){
            String author;
            String whom = "";
            String text;
            String dateTime;
            String commentResult = commentMatcher.group();
            Matcher authorMatcher = Pattern.compile("<b>[.[^<]]*</b>").matcher(commentResult);
            if (authorMatcher.find()){
                String authorResult = authorMatcher.group();
                author = authorResult.substring(3, authorResult.length() - 4);
            } else{
                ErrorManager.sendError("Mestoskidki: SaleComment author for sale not finded! Id: " + saleId + ", cityId: " + cityId + ", comment: " + commentResult);
                continue;
            }
            Matcher whomMatcher = Pattern.compile("  для  [.[^<]]*</b>").matcher(commentResult);
            if (whomMatcher.find()){
                String whomResult = whomMatcher.group();
                whom = whomResult.substring(7, whomResult.length() - 4);
            }
            Matcher textMatcher = Pattern.compile("<p class='comment_text'>[.[^<]]*</p>").matcher(commentResult);
            if (textMatcher.find()){
                String textResult = textMatcher.group();
                text = textResult.substring(24, textResult.length() - 4);
            } else{
                ErrorManager.sendError("Mestoskidki: SaleComment text for sale not finded! Id: " + saleId + ", cityId: " + cityId + ", comment: " + commentResult);
                continue;
            }
            Matcher dateTimeMatcher = Pattern.compile("<p class='comment_add'>[.0-9:[^<]]*</p>").matcher(commentResult);
            if (dateTimeMatcher.find()){
                String dateTimeResult = dateTimeMatcher.group();
                dateTime = dateTimeResult.substring(23, dateTimeResult.length() -4);
            } else{
                ErrorManager.sendError("Mestoskidki: SaleComment datetime for sale not finded! Id: " + saleId + ", cityId: " + cityId + ", comment: " + commentResult);
                continue;
            }
            String[] dateTimeSplit = dateTime.split(" ");
            Date dt;
            try {
                dt = format.parse(dateTime);
            } catch (ParseException e) {
                e.printStackTrace();
                ErrorManager.sendError("Mestoskidki: SaleComment datetime for sale parsing error! Id: " + saleId + ", cityId: " + cityId + ", comment: " + commentResult);
                continue;
            }
            SaleComment saleComment = new SaleComment(saleId, author, whom, dateTimeSplit[0], dateTimeSplit[1], text, dt.getTime());
            result.add(saleComment);
        }
        return result;
    }

    private String getTitle(String page) {
        String result = null;
        Matcher titleMatcher = Pattern.compile("<p class='larger'><strong>[.[^<]]*").matcher(page);
        if (titleMatcher.find()) {
            String titleResult = titleMatcher.group();
            result = titleResult.substring(26);
        }
        return result;
    }

    private String getSubTitle(String page) {
        String result = "";
        Matcher subTitleStrokeMatcher = Pattern.compile("<p class='larger'><strong>[.[^<]]*</strong>[^<]+").matcher(removeBrs(page));
        if (subTitleStrokeMatcher.find()) {
            String subTitleStrokeResult = subTitleStrokeMatcher.group();
            Matcher subTitleMatcher = Pattern.compile("</strong>[^<]+").matcher(subTitleStrokeResult);
            if (subTitleMatcher.find()) {
                String subTitleResult = subTitleMatcher.group();
                result = subTitleResult.substring(9).trim();
            }
        }
        return result;
    }

    private String removeBrs(String text) {
        return text.replaceAll("<br( )?(/)?>", "");
    }

    private String getPeriodStart(String page) {
        String result = null;
        Matcher periodsMatcher = Pattern.compile("<p class='view_sale_date22'>Период акции:<br><br>(<font color='red'>)?[.[^<]]*<").matcher(page);
        if (periodsMatcher.find()) {
            String periodsResult = periodsMatcher.group();
            Matcher periodStartMatcher = Pattern.compile("((<br)|(ed'))>[.[^(<|( ))]]+((<)|( ))").matcher(periodsResult);
            if (periodStartMatcher.find()) {
                String periodStartResult = periodStartMatcher.group();
                result = periodStartResult.substring(4, periodStartResult.length() - 1);
            }
        }
        return result;
    }

    private String getPeriodEnd(String page) {
        String result = null;
        Matcher periodsMatcher = Pattern.compile("<p class='view_sale_date22'>Период акции:<br><br>(<font color='red'>)?[.[^<]]*<").matcher(page);
        if (periodsMatcher.find()) {
            String periodsResult = periodsMatcher.group();
            Matcher periodEndMatcher = Pattern.compile("[0-9] [.[^<]]*<").matcher(periodsResult);
            if (periodEndMatcher.find()) {
                String periodEndResult = periodEndMatcher.group();
                result = periodEndResult.substring(4, periodEndResult.length() - 1);
            }
        }
        return result;
    }

    private String getCoast(String page) {
        String result = "";
        Matcher coastLineMatcher = Pattern.compile("class='view_sale_date22'>Цена: [.[^<]]*<").matcher(page);
        if (coastLineMatcher.find()) {
            String coastLineResult = coastLineMatcher.group();
            Matcher coastMatcher = Pattern.compile(": [.[^<]]*<").matcher(coastLineResult);
            if (coastMatcher.find()) {
                String coastResult = coastMatcher.group();
                result = coastResult.substring(2, coastResult.length() - 1);
            }
        }
        return result;
    }

    private String getQuantity(String page) {
        String result = "";
        Matcher quantityLineMatcher = Pattern.compile("class='view_sale_date22'>((Вес)|(Объем)|(Количество)): [.[^<]]*<").matcher(page);
        if (quantityLineMatcher.find()) {
            String quantityLineResult = quantityLineMatcher.group();
            Matcher quantityMatcher = Pattern.compile(": [.[^<]]*<").matcher(quantityLineResult);
            if (quantityMatcher.find()) {
                String quantityResult = quantityMatcher.group();
                result = quantityResult.substring(2, quantityResult.length() - 1);
            }
        }
        return result;
    }

    private String getQuantityCoast(String page) {
        String result = "";
        Matcher quantityCoastLineMatcher = Pattern.compile("class='view_sale_date22'>Цена за [.[^:]]*: [.[^<]]*").matcher(page);
        if (quantityCoastLineMatcher.find()) {
            String quantityCoastLineResult = quantityCoastLineMatcher.group();
            result = quantityCoastLineResult.substring(25);
        }
        return result;
    }

    private int getShopId(String page) {
        int result = -1;
        Matcher shopLineMatcher = Pattern.compile("<a href=\"view_shop\\.php\\?city=[0-9]*&shop=[0-9]*\" title=").matcher(page);
        if (shopLineMatcher.find()) {
            String shopLineResult = shopLineMatcher.group();
            Matcher shopMatcher = Pattern.compile("shop=[0-9]*").matcher(shopLineResult);
            if (shopMatcher.find()) {
                String shopResult = shopMatcher.group();
                result = Integer.valueOf(shopResult.substring(5));
            }
        }
        return result;
    }

    private int getCategoryId(String page) {
        int result = -1;
        Matcher categoryLineMatcher = Pattern.compile("<a href=\"view_cat\\.php\\?city=[0-9]*&cat[0-9]*=[0-9]*\" title=").matcher(page);
        if (categoryLineMatcher.find()) {
            String categoryLineResult = categoryLineMatcher.group();
            Matcher categoryMatcher = Pattern.compile("cat[0-9]*=[0-9]*").matcher(categoryLineResult);
            if (categoryMatcher.find()) {
                String categoryResult = categoryMatcher.group();
                String[] split = categoryResult.split("=");
                result = Integer.valueOf(split[1]);
            }
        }
        return result;
    }

    private int getCategoryType(String page) {
        int result = -1;
        Matcher categoryLineMatcher = Pattern.compile("<a href=\"view_cat\\.php\\?city=[0-9]*&cat[0-9]*=[0-9]*\" title=").matcher(page);
        if (categoryLineMatcher.find()) {
            String categoryLineResult = categoryLineMatcher.group();
            Matcher categoryMatcher = Pattern.compile("cat[0-9]*=[0-9]*").matcher(categoryLineResult);
            if (categoryMatcher.find()) {
                String categoryResult = categoryMatcher.group();
                String[] split = categoryResult.split("=");
                if (split[0].replace("cat", "").isEmpty()) {
                    result = 1;
                } else {
                    result = Integer.valueOf(split[0].replace("cat", ""));
                }
            }
        }
        return result;
    }*/
}
