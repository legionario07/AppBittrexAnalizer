package br.com.bittrexanalizer.strategy;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.LinkedList;

import br.com.bittrexanalizer.domain.Order;

/**
 * Created by PauLinHo on 17/01/2018.
 */

public class OrderStrategy implements IStrategy<Order> {

    private LinkedList<Order> objetos;


    @Override
    public LinkedList<Order> getObjects(String dados) {

        objetos = new LinkedList<>();


        dados = dados.replace("}", "");

        String[] dadosTemp = dados.split("\\{");

        for (int i = 2; i < dadosTemp.length; i++) {

            objetos.add(getOrders(dadosTemp[i]));
        }

        return objetos;
    }

    private static Order getOrders(String dados) {

        Order order = new Order();

        /*	"Uuid" : null,
    "OrderUuid" : "09aa5bb6-8232-41aa-9b78-a5a1093e0211",
	"Exchange" : "BTC-LTC",
	"OrderType" : "LIMIT_SELL",
	"Quantity" : 5.00000000,
	"QuantityRemaining" : 5.00000000,
	"Limit" : 2.00000000,
	"CommissionPaid" : 0.00000000,
	"Price" : 0.00000000,
	"PricePerUnit" : null,
	"Opened" : "2014-07-09T03:55:48.77",
	"Closed" : null,
	"CancelInitiated" : false,
	"ImmediateOrCancel" : false,
	"IsConditional" : false,
	"Condition" : null,
	"ConditionTarget" : null*/

        String[] dadosTemp = dados.split(",");

        for (String s : dadosTemp) {

            String key[] = s.replace("]", "").split(":");

            switch (key[0].replace("\"", "").replace("\"", "")) {
                case "Uuid":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        order.setUuid("");
                    else
                        order.setUuid(key[1].replace("\"", ""));
                    break;

                case "AccountId":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        order.setAccountId(null);
                    else
                        order.setAccountId(new Long(key[1].replace("\"", "")));
                    break;

                case "OrderUuid":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        order.setOrderUuid("");
                    else
                        order.setOrderUuid(key[1].replace("\"", ""));
                    break;
                case "Exchange":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        order.setExchange("");
                    else {
                        order.setExchange(key[1].replace("\"", ""));
                        String[] sigla = order.getExchange().split("-");
                        order.setSigla(sigla[1]);
                    }
                    break;
                case "OrderType":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        order.setOrderType("");
                    else
                        order.setOrderType(key[1].replace("\"", ""));
                    break;
                case "Type":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        order.setType("");
                    else
                        order.setType(key[1].replace("\"", ""));
                    break;
                case "Quantity":
                    if (key[1].equals("0.00000000"))
                        order.setQuantity(new BigDecimal("0.0"));
                    else
                        order.setQuantity(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "QuantityRemaining":
                    if (key[1].equals("0.00000000"))
                        order.setQuantityRemaining(new BigDecimal("0.0"));
                    else
                        order.setQuantityRemaining(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "Limit":
                    if (key[1].equals("0.00000000"))
                        order.setLimit(new BigDecimal("0.0"));
                    else
                        order.setLimit(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "Reserved":
                    if (key[1].equals("0.00000000"))
                        order.setReserved(new BigDecimal("0.0"));
                    else
                        order.setReserved(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "ReserveRemaining":
                    if (key[1].equals("0.00000000"))
                        order.setReserveRemaining(new BigDecimal("0.0"));
                    else
                        order.setReserveRemaining(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "Comission":
                    if (key[1].equals("0.00000000"))
                        order.setComission(new BigDecimal("0.0"));
                    else
                        order.setComission(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "CommissionReserved":
                    if (key[1].equals("0.00000000"))
                        order.setComissionPaid(new BigDecimal("0.0"));
                    else
                        order.setComissionPaid(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "CommissionReservedRemaining":
                    if (key[1].equals("0.00000000"))
                        order.setComissionPaid(new BigDecimal("0.0"));
                    else
                        order.setComissionPaid(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "CommissionPaid":
                    if (key[1].equals("0.00000000"))
                        order.setComissionPaid(new BigDecimal("0.0"));
                    else
                        order.setComissionPaid(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "Price":
                    if (key[1].equals("0.00000000"))
                        order.setPrice(new BigDecimal("0.0"));
                    else
                        order.setPrice(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "PricePerUnit":
                    if (key[1].toLowerCase().replace("\"", "").equals("null") || key[1].equals("0.00000000"))
                        order.setPricePerUnit(BigDecimal.ZERO);
                    else
                        order.setPricePerUnit(new BigDecimal(key[1].replace("\"", "")));
                    break;
                case "Opened":

                    if (key[1].toString().toLowerCase().replace("\"", "").equals("null"))
                        order.setOpened(null);
                    else {

                        StringBuilder data = new StringBuilder();
                        data.append(key[1].trim().replace("T", " "));
                        data.append(":");
                        data.append(key[2].trim());
                        data.append(":");
                        String dataSplit[] = key[3].trim().replace(".",":").split(":");
                        data.append(dataSplit[0]);

                        String dataFinal = data.toString().replace("\"", "").replace("-","/");

                        try {
                            order.getOpened().setTime(SDF_DDMMYYYY_HHMMSS.parse(dataFinal));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "Closed":

                    if (key[1].toString().toLowerCase().replace("\"", "").equals("null"))
                        order.setClosed(null);
                    else {

                        StringBuilder dataClosed = new StringBuilder();
                        dataClosed.append(key[1].trim().replace("T", " "));
                        dataClosed.append(":");
                        dataClosed.append(key[2].trim());
                        dataClosed.append(":");
                        String dataSplitClosed[] = key[3].trim().replace(".",":").split(":");
                        dataClosed.append(dataSplitClosed[0]);

                        String dataFinal = dataClosed.toString().replace("\"", "").replace("-","/");

                        try {
                            order.getClosed().setTime(SDF_DDMMYYYY_HHMMSS.parse(dataFinal));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "TimeStamp":

                    if (key[1].toString().toLowerCase().replace("\"", "").equals("null"))
                        order.setTimeStamp(null);
                    else {

                        StringBuilder dataClosed = new StringBuilder();
                        dataClosed.append(key[1].trim().replace("T", " "));
                        dataClosed.append(":");
                        dataClosed.append(key[2].trim());
                        dataClosed.append(":");
                        String dataSplitClosed[] = key[3].trim().replace(".",":").split(":");
                        dataClosed.append(dataSplitClosed[0]);

                        String dataFinal = dataClosed.toString().replace("\"", "").replace("-","/");

                        try {
                            order.getTimeStamp().setTime(SDF_DDMMYYYY_HHMMSS.parse(dataFinal));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "CancelInitiated":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        order.setCancelInitiated(false);
                    else
                        order.setCancelInitiated(Boolean.valueOf(key[1].replace("\"", "")));
                    break;
                case "ImmediateOrCancel":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        order.setImmediateOrCancel(false);
                    else
                        order.setImmediateOrCancel(Boolean.valueOf(key[1].replace("\"", "")));
                    break;
                case "IsConditional":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        order.setIsConditional(false);
                    else
                        order.setIsConditional(Boolean.valueOf(key[1].replace("\"", "")));
                    break;
                case "Condition":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        order.setCondition("");
                    else
                        order.setCondition(key[1].replace("\"", ""));
                    break;
                case "ConditionTarget":
                    if (key[1].toLowerCase().replace("\"", "").equals("null"))
                        order.setConditionTarget("");
                    else
                        order.setConditionTarget(key[1].replace("\"", ""));
                    break;

            }

        }

        return order;
    }
}
