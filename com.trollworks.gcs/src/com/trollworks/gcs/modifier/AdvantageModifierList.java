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

package com.trollworks.gcs.modifier;

import com.trollworks.gcs.datafile.ListFile;
import com.trollworks.gcs.datafile.LoadState;
import com.trollworks.gcs.ui.RetinaIcon;
import com.trollworks.gcs.ui.image.Images;
import com.trollworks.gcs.ui.widget.outline.OutlineModel;
import com.trollworks.gcs.ui.widget.outline.Row;
import com.trollworks.gcs.utility.FileType;
import com.trollworks.gcs.utility.xml.XMLNodeType;
import com.trollworks.gcs.utility.xml.XMLReader;

import java.io.IOException;

/** Data Object to hold several {@link AdvantageModifier}s */
public class AdvantageModifierList extends ListFile {
    private static final int    CURRENT_VERSION = 1;
    /** The XML tag for advantage modifier lists. */
    public static final  String TAG_ROOT        = "modifier_list";

    /** Creates new {@link AdvantageModifierList}. */
    public AdvantageModifierList() {
    }

    /**
     * Creates a new {@link AdvantageModifierList}.
     *
     * @param modifiers The {@link AdvantageModifierList} to clone.
     */
    public AdvantageModifierList(AdvantageModifierList modifiers) {
        this();
        for (Row Row : modifiers.getModel().getRows()) {
            getModel().getRows().add(Row);
        }
    }

    @Override
    protected void loadList(XMLReader reader, LoadState state) throws IOException {
        OutlineModel model  = getModel();
        String       marker = reader.getMarker();
        do {
            if (reader.next() == XMLNodeType.START_TAG) {
                String name = reader.getName();

                if (AdvantageModifier.TAG_MODIFIER.equals(name) || AdvantageModifier.TAG_MODIFIER_CONTAINER.equals(name)) {
                    model.addRow(new AdvantageModifier(this, reader, state), true);
                } else {
                    reader.skipTag(name);
                }
            }
        } while (reader.withinMarker(marker));
    }

    @Override
    // Not used
    public FileType getFileType() {
        return FileType.ADVANTAGE_MODIFIER;
    }

    @Override
    // Not used
    public RetinaIcon getFileIcons() {
        return Images.ADM_FILE;
    }

    @Override
    public int getXMLTagVersion() {
        return CURRENT_VERSION;
    }

    @Override
    public String getXMLTagName() {
        return TAG_ROOT;
    }
}
