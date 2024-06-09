package dev.emperor.utils;

import top.fl0wowp4rty.phantomshield.annotations.Native;
import top.fl0wowp4rty.phantomshield.annotations.obfuscation.CodeVirtualization;

@Native
public class TimerUtil {
   private boolean run = true;
   private long time = System.currentTimeMillis();

   public TimerUtil(boolean run) {
      this.run = run;
   }

   public void start() {
      this.run = true;
   }

   public void stop() {
      this.run = false;
   }

   @CodeVirtualization("FISH_RED")
   public void reset() {
      this.time = System.currentTimeMillis();
   }

   @CodeVirtualization("FISH_RED")
   public long getElapsedTime() {
      return this.run ? System.currentTimeMillis() - this.time : 0L;
   }

   @CodeVirtualization("FISH_RED")
   public boolean hasTimeElapsed(long milliseconds) {
      return this.run && this.getElapsedTime() >= milliseconds;
   }

   @CodeVirtualization("FISH_RED")
   public void delay(long milliseconds) {
      this.time += milliseconds;
   }

   @CodeVirtualization("FISH_RED")
   public static long getCurrentTime() {
      return System.currentTimeMillis();
   }

   @CodeVirtualization("FISH_RED")
   public boolean isOver(long milliseconds) {
      return System.currentTimeMillis() - this.time > milliseconds;
   }

   @CodeVirtualization("FISH_RED")
   public long remainingTime(long milliseconds) {
      long elapsedTime = System.currentTimeMillis() - this.time;
      return elapsedTime < milliseconds ? milliseconds - elapsedTime : 0L;
   }

   public boolean isRun() {
      return this.run;
   }

   public long getTime() {
      return this.time;
   }

   public void setRun(boolean run) {
      this.run = run;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public TimerUtil() {
   }
}
