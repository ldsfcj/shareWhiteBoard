package ui;

import java.io.Serializable;

public class UserWhiteBoard extends WhitePaintBoard implements Serializable {

    public UserWhiteBoard(String username){
        super(username);
        getUserListBoard().getKickButton().setVisible(false);
    }

    @Override
    public void initMenuBar() {

    }
}
