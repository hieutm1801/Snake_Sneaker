package hieutm.dev.snakesneaker.interfaces;

import java.io.Serializable;

public interface OnClickSend extends Serializable {

    void onClick(String id, String type,String size,int position);

}
