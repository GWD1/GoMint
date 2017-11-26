/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.inventory;

import io.gomint.math.BlockPosition;

/**
 * @author geNAZt
 * @version 1.0
 */
public interface ContainerInventory {

    /**
     * Get the position of the container
     *
     * @return block position of this container
     */
    BlockPosition getContainerPosition();

}
