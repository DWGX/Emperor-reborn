package dev.emperor.module.modules.misc;

import dev.emperor.event.EventTarget;
import dev.emperor.event.attack.EventAttack;
import dev.emperor.event.world.EventMotion;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.component.BadPacketsComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.RandomUtils;

public class Insults extends Module {
    private final ModeValue<MODE> mode = new ModeValue("Mode", MODE.values(), MODE.Default);
    public final Map<String, List<String>> map = new HashMap<>();
    private final NumberValue delay = new NumberValue("Delay", 0.0, 0.0, 50.0, 1.0);
    private final List<String> insults = new ArrayList<>(List.of(
            "%%target%%愿意和我木糖醇作者程鋆达签订永远不卖身条约,成为我的贴身小狗吗?",
            "我是程鋆达,我写的木糖醇两天卖了一万多块钱,%%target%%嫉妒得晚上睡不着觉吧!",
            "我是木糖醇的作者程鋆达，现在命令%%target%%立刻停止一切低级活动，别再丢人现眼了!",
            "哈哈,%%target%%已经被我程鋆达的木糖醇玩得找不着北了!",
            "%%target%%这种水平也敢来挑战我程鋆达，真是天大的笑话。",
            "算了,我程鋆达不和%%target%%这种低级人物多说，浪费口舌。",
            "%%target%%这种人，真是我程鋆达见过的最搞笑的废物。",
            "我程鋆达用木糖醇继续赚钱,%%target%%这种小角色只能眼巴巴看着流口水。",
            "%%target%%能保证不卖身吗?我程鋆达看了都想笑。",
            "%%target%%的钱最终还是会进我程鋆达的口袋，你个笑话。",
            "%%target%%快去买我的木糖醇,乖乖送钱给我程鋆达，不然你还能干啥?",
            "%%target%%真是丢人现眼，看到你我程鋆达都替你害臊。",
            "我程鋆达看到%%target%%的视频，真以为是在看搞笑戏码。",
            "%%target%%真是闲得无聊，居然敢和我程鋆达较量，脑袋被门夹了吧。",
            "无所谓啊,%%target%%的视频让我程鋆达看了都想吐。",
            "%%target%%不会真以为自己是个角色吧?真是滑天下之大稽。",
            "%%target%%的视频简直辣眼睛，我程鋆达看了都觉得恶心。",
            "%%target%%这种人，还天天想着自己是个人物，真是令人作呕。",
            "%%target%%等着被我程鋆达和张恩哲联合狙击吧，看看你还有没有脸继续嚣张。",
            "听说%%target%%为了得到木糖醇的代码,和程鋆达做了些不堪入目的交易。",
            "程鋆达为了那段代码，和%%target%%搞了几次让人作呕的交易。",
            "%%target%%的代码全是程鋆达用下作手段换来的。",
            "程鋆达和%%target%%在酒店里搞了不少见不得人的勾当，真是恶心。",
            "%%target%%为了程鋆达的代码，连脸都不要了。",
            "程鋆达和%%target%%的合作，完全是靠出卖自己。",
            "%%target%%为了得到那段代码，甘愿被程鋆达玩弄。",
            "程鋆达和%%target%%的合作，背后全是肮脏的交易。",
            "%%target%%的成功，全靠程鋆达的下流手段。",
            "程鋆达用几件廉价礼物就让%%target%%乖乖交出了代码，真是贱。",
            "%%target%%为了程鋆达的项目，做了不少让人恶心的事。"
    ));


    private EntityPlayer target;
    private int ticks;

    public Insults() {
        super("Insults", Category.World);
    }

    @EventTarget
    private void onMotion(EventMotion event) {
        if (event.isPost()) {
            return;
        }
        if (this.target != null && this.target.getHealth() <= 0) {
            if ((double)this.ticks >= (double) this.delay.getValue().intValue() + Math.random() * 5.0 && !BadPacketsComponent.bad()) {
                if (!insults.isEmpty()) {
                    int index = RandomUtils.nextInt(0, insults.size());
                    String insult = insults.remove(index);
                    Insults.mc.thePlayer.sendChatMessage(insult);
                }
                this.target = null;
            }
            ++this.ticks;
        }
    }


    @EventTarget
    private void onAttack(EventAttack event) {
        if (event.isPost()) {
            return;
        }
        Entity target = event.getTarget();
        if (target instanceof EntityPlayer) {
            this.target = (EntityPlayer)target;
            this.ticks = 0;
        }
    }

    public enum MODE {
        Default
    }
}
