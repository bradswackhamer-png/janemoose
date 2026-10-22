package com.swack.janemoose.item;

import com.swack.janemoose.entity.JaneMooseAttachmentEntity;
import com.swack.janemoose.entity.JaneMooseTractorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public interface IAttachment {
    enum AttachmentType {
        CULTIVATOR,
        HARVESTER,
        PLANTER
    }

    AttachmentType getType();

    /**
     * Width of operation: 1 for 1x1, 2 for 1x2, 3 for 1x3
     */
    int getWidth();

    /**
     * Executes the attachment's farming operation at the specified ground/crop position.
     * @return true if an operation was successfully performed
     */
    boolean processBlock(ServerLevel level, BlockPos pos, JaneMooseTractorEntity tractor, JaneMooseAttachmentEntity attachment, Player rider);
}
