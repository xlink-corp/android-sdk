/**
 * Created by wen on 15/6/16.
 * food webapp base.js
 */

"use strict";
(function(Z, win){


    Z.extend(Z.fn, {
        display : function(b){
            var len = this.length, jq;
            if(len){
                if(len === 1){
                    if(b === undefined){
                        var v = !this.hasClass('hidden');
                        return v;
                    }else {
                        if(b) this.removeClass('hidden');
                        else this.addClass('hidden');
                    }
                }else {
                    this.each(function(){
                        if(b) Z(this).removeClass('hidden');
                        else Z(this).addClass('hidden');
                    });
                }
            }
            return this;
        }
    });

    var u = navigator.userAgent, app = navigator.appVersion,
        isMoblie = !!u.match(/AppleWebKit.*Mobile.*/),
        eventType = isMoblie ? 'tap' : 'click', touchEvent = 'touchend';




    var Food = {

        isModule : isMoblie,

        myFastClick : function(etype, el, fn){
            var zel = $(el), dom = $(el).get(0), evt, preventDefault = true, stopPropagation = true;
            if($.type(etype) === 'object'){
                evt = etype.evt;
                preventDefault = etype.hasOwnProperty('pd') ? etype.pd : preventDefault;
                stopPropagation = etype.hasOwnProperty('sp') ? etype.sp : stopPropagation;
            }else{
                evt = etype;
            }
            // $('#evt').text(touchEvent)
            $(el).on(touchEvent, function (e) {
                var event = $.Event(evt);
                //这里为了方便而已，其实该e.target
                dom.dispatchEvent(event);
                preventDefault && e.preventDefault();
                stopPropagation && e.stopPropagation();
            });
            $(el).on(evt, fn);
        },

        eventType : eventType,

        //广播事件
        on : function(name, fn){
            Food.actions.reg('self_'+name, fn);
            return this;
        },

        fire : function(name, params){
            var fns = Food.actions.get('self_'+name);
            if(fns){
                if(Z.type(fns) === 'array'){
                    Z.each(fns, function(i, fn){
                        fn(params);
                    })
                }else{
                    fns(params);
                }
                //return Gaofen.actions.get('self_'+name)(params);
            }else{
                //console || console.log('no event');
            }
        },

        util : {
            bind : function(fn, scope){
                return function() {
                    return fn.apply(scope, arguments);
                };
            },
            parse : function(htmls, map){
                if(htmls){
                    var tplReg =  /\{(\.?[\w_|$]+)(\.[\w_$]+)?\}/g;
                    return htmls.replace(tplReg, function(s, k , k1){
                        var v, modfs, k_str, key;
                        if (k.charCodeAt(0) === 46)  {
                            k_str = k.substr(1);
                            modfs = k_str.split('|');
                            key = modfs.shift();
                            v = map[key] === undefined? '' : map[key];
                        }
                        return v;
                    });
                }else
                    return '';
            },
            parseKnV : function(strRel){
                var map = {}, kv, kvs = strRel.split(',');
                try {
                    for( var i=0,len=kvs.length;i<len;i++){
                        // if not contains ':'
                        // set k = true
                        if(kvs[i].indexOf(':') === -1){
                            map[kvs[i]] = true;
                        }else {
                            // split : to k and v
                            kv = kvs[i].split(':');
                            // escape value
                            map[kv[0]] = kv[1];
                        }
                    }
                }catch(e) {
                    throw 'Syntax Error:rel字符串格式出错。' + strRel;
                }

                return map;
            }

            ,isEmail : function(txt){
                return /.+@.+\.[a-zA-Z]{2,4}$/.test(txt);
            }

            ,byteLen : function(text){
                var len = text.length;
                var matcher = text.match(/[^\x00-\xff]/g);
                if(matcher)
                    len += matcher.length;
                return len;
            },


            getStorage : function(key){//缓存本地数据（24小时内有效）
                var data = win.localStorage.getItem(key), time = +new Date;
				if (!data)
					return '';
				data = JSON.parse(data);
				if (time - data.time > 86400000) {

				} else {
					return data.data;
				}
            },


            setStorage : function(key, data){
                var d;

                if(Z.type(data) === 'object'){
                   d = JSON.stringify(data);
                }else{
                    d = data;
                }

                win.localStorage.setItem(key, JSON.stringify({
									time : +new Date,
									data : d
								}));
            }
        },


        tips : {
            alert : function(msg, type){//type : success\warning\error
                if(typeof toastr)
                    toastr[type || 'success'](msg||'未知错误！');
                else
                    alert(msg);
            }
        },

        event : function(cb){
            Food.myFastClick(eventType, Z('body'),function(e){
//            Z('body').on(eventType, function(e){
                var tg = Z(e.target), rel = tg.attr('rel'), data = [];
                if(tg.data('lock')){
                    e.preventDefault();
                    return;
                }
                for(var i=0;i<10;i++){
                    var _rel = tg.attr('rel');

                    if(_rel){
                        var item = Food.util.parseKnV(_rel);
                        item.target = tg;
                        data.push(item);
                    }
                    tg = tg.parent();
                }
                var len = data.length;
                if(len){
                    for(var j = 0;j<len;j++){
                        var evt = Food.actions.get(data[j]['e']);
                        //console.log('event:'+data[j]['e']);
                        if(evt){
                            if(!evt(e, data[j]))
                                e.preventDefault();
                        }
                    }
                }
                Food.fire('global-bodyClick');
            })
        },

        actions : {
            evts : {},
            get : function(ns){
                return this.evts[ns];
            },

            reg : function(ns, fn){
                if(!this.evts[ns])
                    this.evts[ns] = fn;
                else{
                    var orfn = this.evts[ns];
                    if(Z.type(orfn) === 'array'){
                        orfn.push(fn);
                    }else{
                        var newfn = [orfn];
                        newfn.push(fn);
                        this.evts[ns] = newfn;
                    }

                }
                return this;
            },
            del : function(ns){
                var fn = this.get(ns);
                if(fn){
                    return delete this.evts[ns];
                }
                return '';
            }
        },


        getRouter : function(url, router){

        },



        cls : {}

    }

    /**
     *	Ajax请求入口
     * @param {String} url 请求地址
     * @param {Object} param 参数
     * @param {Function} fn  回调
     * @param {String} method ajax类型
     **/

    Food['Ajax'] = {

        send : function(url, param, fn, method){
            if( !param ) param = {__rnd : +new Date};
            else param['__rnd'] = +new Date;
            method = method || 'get';
            switch(method){
                case 'get' :
                    $.get(url, param, fn);
                    break;
                case 'post' :
                    $.post(url, param, fn);
                    break;
                case 'jsonp' :
                    $.getJSON(url, param, fn);
                    break;
            }

        }

    };

    Food.PD = (function(){

        if(typeof Food.cfg === 'undefined')
            Food.cfg = {};
        var data = Food.cfg,
            _window = {};
        return {
            set : function(name, _data){
                var len = arguments.length;
                if (len == 3){
                    _window[name] = _data
                    data[name] = _data;
                }else if(len == 2){
                    $.extend(data, _data);
                }else if(len == 1){
                    $.extend(data, name);
                }
            },

            get : function(name){
                var len = arguments.length;
                if(len == 1)
                    return data[name];
                else{
                    if(len === 0) return Food.cfg;
                    return _window[name];
                }
            }
        }

    })()



    window['Food'] = Food;


})(Zepto, window);
