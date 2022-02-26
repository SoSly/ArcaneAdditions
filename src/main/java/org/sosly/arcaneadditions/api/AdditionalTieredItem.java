/*
 *   Arcane Additions Copyright (c)  2022, Kevin Kragenbrink <kevin@writh.net>
 *           This program comes with ABSOLUTELY NO WARRANTY; for details see <https://www.gnu.org/licenses/gpl-3.0.html>.
 *           This is free software, and you are welcome to redistribute it under certain
 *           conditions; detailed at https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.sosly.arcaneadditions.api;

import com.mna.api.items.TieredItem;

public class AdditionalTieredItem extends TieredItem {
    public AdditionalTieredItem(Properties props) {
        super(props.tab(AACreativeTab.TAB_AA));
    }
}
