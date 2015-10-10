//package cl.chihau.helloworldwear;
//
//import com.google.android.gms.wearable.DataEventBuffer;
//import com.google.android.gms.wearable.WearableListenerService;
//
//public class MyService extends WearableListenerService {
//
//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        for (DataEvent dataEvent : dataEvents) {
//            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
//                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
//                String path = dataEvent.getDataItem().getUri().getPath();
//                if (path.equals("/step-counter")) {
//                    int steps = dataMap.getInt("step-count");
//                    long time = dataMap.getLong("timestamp");
//
//                    Log.d("TEST", "steps: " + steps + " - " + "timestamp: " + time);
//
//                    Intent i = new Intent("LOCATION_UPDATED");
//                    i.putExtra("<Key>","text");
//
//                    sendBroadcast(i);
//                }
//            }
//        }
//    }
//}
