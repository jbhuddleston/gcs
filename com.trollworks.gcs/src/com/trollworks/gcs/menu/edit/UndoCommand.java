/*
 * Copyright ©1998-2020 by Richard A. Wilkes. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, version 2.0. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, version 2.0.
 */

package com.trollworks.gcs.menu.edit;

import com.trollworks.gcs.menu.Command;
import com.trollworks.gcs.menu.StdMenuBar;
import com.trollworks.gcs.utility.I18n;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.undo.UndoManager;

/** Provides the "Undo" command. */
public class UndoCommand extends Command {
    /** The action command this command will issue. */
    public static final String CMD_UNDO = "Undo";

    /** The singleton {@link UndoCommand}. */
    public static final UndoCommand INSTANCE = new UndoCommand();

    private UndoCommand() {
        super(I18n.Text("Can't Undo"), CMD_UNDO, KeyEvent.VK_Z);
    }

    @Override
    public void adjust() {
        if (StdMenuBar.SUPRESS_MENUS) {
            setEnabled(false);
            return;
        }
        Undoable undoable = getTarget(Undoable.class);
        if (undoable != null) {
            UndoManager mgr = undoable.getUndoManager();
            setEnabled(mgr.canUndo());
            setTitle(mgr.getUndoPresentationName());
        } else {
            setEnabled(false);
            setTitle(I18n.Text("Can't Undo"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Undoable undoable = getTarget(Undoable.class);
        if (undoable != null) {
            undoable.getUndoManager().undo();
        }
    }
}
