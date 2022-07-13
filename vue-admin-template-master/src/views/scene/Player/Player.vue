<template>
  <div class="AudioViewPlayer" >
      <div class="Control">

        <div class="Buttons">
          <audio></audio>
          <span class="Play-Pause" @click="play_pause">
            <svg-icon v-if="isPlaying" icon-class="pause" className="player"></svg-icon>
            <svg-icon v-else icon-class="play" className="player"></svg-icon>
          </span>


          <span class="File" @click="loadfile">
            <svg-icon  icon-class="file"  className="file"></svg-icon>
          </span>
          <div class="Text" style="width: 85%;text-align: center">
            <i v-if="fileName==''">暂无文件加载</i>
            <i v-else>{{ fileName }}</i>
            <el-slider  v-model="playedPercentage" :show-tooltip="false" style="width:100%" @change="changeprocess"></el-slider>
          </div>



        </div>





        <!--          <button @click="play" class="Play">播放</button>-->
        <!--          <button @click="pause" class="Pause">暂停</button>-->
        <!--          <input id="MP3File" @change="LoaderFile" type="file" class="Select"/>-->
        <!--&lt;!&ndash;          <el-progress :percentage="playedPercentage" :show-text=false></el-progress>&ndash;&gt;-->
        <!--          <el-slider v-model="playedPercentage" :show-tooltip="false"></el-slider>-->
      </div>



  </div>
</template>

<script>

export default {
    name:'AudioPlayer',
    data() {
        return {
            musicBuffer:null,
            //缓冲数据
            isLoading:false,
            //判断是否加载音乐
            isPlaying:false,
            //是否正在播放音乐
            fileName:'',
            //音乐名
            playedTime:0,
            //播放时间
            audioCtx:null,
            //音频上下文
            audioBufferSourceNode:null,
            //音频节点
            fileReader:null,
            //文件读取
            analyser:null,
            //分析器
            fileDuration:0,
            //音乐时长
            timer:0,

          //playedPercentage: 0
            //playedPercentage:0
          //playedPercentage:0

        }
    },
    computed:{
        playedPercentage:{
            get(){
                if(this.fileDuration>0){
                    return this.playedTime*100/this.fileDuration;

                }else{
                    return 0;
                }
            },
            set(percentage){

                this.playedTime=percentage*this.fileDuration/100;

            }
        }
    },
   watch:{
        isPlaying:{
            handler(){
                this.$bus.$emit('PlayState',this.isPlaying);
            },
            deep:true,
            immediate:true
        }
    },
    mounted() {
        this.initContext();


    },
    methods: {
      changeprocess(){
        if(!this.isLoading){
          this.$message.error("未加载媒体");
          this.playedTime=0;
        }else{
          if(this.isPlaying){
            //正在播放时改变
            this.play_pause();
            this.play_pause();
          }
          else{
            //暂停时改变
            this.play_pause();
          }
        }
      },

      loadfile(){
        let that=this;
        let file;
        let input=document.createElement('input');
        input.type="file";
        input.addEventListener("change",function(){
             file=input.files[0];
          that.fileName=file.name;
          that.filereader=new FileReader();
          that.filereader.readAsArrayBuffer(file);
          that.filereader.onload=(event)=>{
            that.audioCtx.decodeAudioData(event.target.result,(buffer)=>{
              that.musicBuffer=buffer;
              //先存音频数据
              that.isLoading=true;
              that.fileDuration=buffer.duration;

              that.AnalyserData();
            })
            //音频数据
          }
        });
        input.click();
      },
      play_pause(){
        const player=document.querySelector('.Play-Pause');
        //console.log(player);
        if(!this.isLoading){
          this.$message.error("未加载媒体");

        }else{
          if(!this.isPlaying){
            //只有每次播放时才建立，因为start只能一次
            this.audioBufferSourceNode=this.audioCtx.createBufferSource();
            //一开始进行初始化
            this.audioBufferSourceNode.buffer=this.musicBuffer;
            //播放时才去加载数据
            this.audioBufferSourceNode.connect(this.audioCtx.destination);
            //连接
            this.audioBufferSourceNode.connect(this.analyser);
            //连接分析器
            this.audioBufferSourceNode.start(0,this.playedTime);
            //播放
            player.classList.add('active');

            this.isPlaying=true;
            this.timer=setInterval(()=>{
              if(this.playedTime<this.fileDuration){
                this.playedTime+=0.1;
              }
              else{
                this.playedTime=0;
                this.audioBufferSourceNode.stop();
                this.audioBufferSourceNode.disconnect();
                this.isPlaying=false;
                player.classList.remove('active');
                clearInterval(this.timer);

              }
            },100);
            //实时更新进度条，每100毫秒即0.1秒更新一次，
          }
          else{
            this.audioBufferSourceNode.stop();
            this.audioBufferSourceNode.disconnect();
            this.isPlaying=false;
            player.classList.remove('active');
            clearInterval(this.timer);
          }

        }

      },
        initContext(){
            const AudioContext = window.AudioContext || window.webkitAudioContext;
            this.audioCtx=new AudioContext();
            //创建上下文

            //创建缓冲节点
           this.analyser=this.audioCtx.createAnalyser();
        },
      // play(){
      //       if(!this.isLoading){
      //           alert('未加载媒体');
      //       }else{
      //           if(!this.isPlaying){
      //              //只有每次播放时才建立，因为start只能一次
      //             this.audioBufferSourceNode=this.audioCtx.createBufferSource();
      //               //一开始进行初始化
      //           this.audioBufferSourceNode.buffer=this.musicBuffer;
      //           //播放时才去加载数据
      //           this.audioBufferSourceNode.connect(this.audioCtx.destination);
      //           //连接
      //           this.audioBufferSourceNode.connect(this.analyser);
      //           //连接分析器
      //           this.audioBufferSourceNode.start(this.playedTime,this.playedTime);
      //           //播放
      //
      //
      //           this.isPlaying=true;
      //           this.timer=setInterval(()=>{
      //               if(this.playedTime<this.fileDuration){
      //                   this.playedTime+=0.1;
      //               }
      //               else{
      //                   this.playedTime=0;
      //                   //this.isPlaying=false;
      //                   clearInterval(this.timer);
      //
      //               }
      //           },100);
      //           //实时更新进度条，每100毫秒即0.1秒更新一次，
      //           }
      //
      //       }
      //
      //
      //   },
      //   pause(){
      //       if(!this.isLoading){
      //           alert('未加载媒体');
      //       }
      //       else{
      //           if(this.isPlaying){
      //           this.audioBufferSourceNode.stop();
      //           this.audioBufferSourceNode.disconnect();
      //            this.isPlaying=false;
      //             this.player.classList.remove(' active');
      //           clearInterval(this.timer);
      //           }
      //
      //
      //       }
      //
      //   },
      //   LoaderFile(e){
      //       let file=e.target.files[0];
      //       this.fileName=file.name;
      //       this.filereader=new FileReader();
      //       this.filereader.readAsArrayBuffer(file);
      //       this.filereader.onload=(event)=>{
      //       this.audioCtx.decodeAudioData(event.target.result,(buffer)=>{
      //           this.musicBuffer=buffer;
      //           //先存音频数据
      //           this.isLoading=true;
      //           this.fileDuration=buffer.duration;
      //
      //           this.AnalyserData();
      //       })
      //       //音频数据
      //       }
      //   },
        AnalyserData(){
            this.analyser.fftSize=512;
            //512是最合适的值
            // let bufferLength=analyser.frequencyBinCount;
            // let dataArray=new Uint8Array(bufferLength);
            // this.analyser.getByteFrequencyData(dataArray);
            this.$bus.$emit('BindingAnalyser',this.analyser);
        }

    },
}
</script>

<style lang='scss' >
.Control{

  position: relative;
  width: 100%;
  height: 85px;
  background: #fbfbfb;
  border: 2px solid #fff;
  padding: 20px 30px;
  box-shadow: -10px -10px 15px #fff,
              10px 10px 15px rgba(0, 0, 0, 0.1),
              inset -5px -5px 15px #fff,
              inset 5px 5px 15px rgba(0,0,0,0.1);
  .Buttons{
    position: relative;
    display: flex;
    justify-content: space-around;
    align-items: center;
    width:100%;
    .Play-Pause{
      .player{
        font-size: 3em;
      }
    }
    .File{
      .file{
        font-size: 1.5em;
      }
    }

  }
  .Buttons span{
    position: relative;
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background: #ffffff;
    display: flex;
    justify-content: center;
    align-items: center;
    //color: #777777;
    cursor: pointer;
    box-shadow: inset 0 -5px 5px rgba(0,0,0,0.1),
                0 5px 10px rgba(0,0,0,0.1),
                0 10px 15px rgba(0,0,0,0.1);
  }
  .Buttons span.active{
    box-shadow: inset 0 -7px 5px rgba(0,0,0,0.025),
                inset 0 5px 10px rgba(0,0,0,0.15);
  }
  .Buttons span:active{
    color: #995f99;
    box-shadow: inset 0 -7px 5px rgba(0,0,0,0.025),
    inset 0 5px 10px rgba(0,0,0,0.15);
  }




}
</style>
