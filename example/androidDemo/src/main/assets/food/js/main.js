/**
 * Created by wen on 15/6/16.
 */
"use strict";
var code_fn;
var group_fn;
var food_fn;
function onCode(text) {
	code_fn(text);
}
function onGroup(text) {
	group_fn(text);
}
function onFood(text) {
	food_fn(text);
}

(function(Z, win, F) {

	// 路由
	var Util = F.util,

	Ajax = F.Ajax.send,

	_group_list = null,

	_foods_list = {},

	_now_food = null,

	prefix = '/food', // 服务器前缀

	pages = {
		'page_1' : {
			index : 0,
			name : '分类',
			back : '',
			backname : ''
		},
		'page_2' : {
			index : 1,
			code : '',
			name : '具体分类',
			back : 'page_1',
			backname : '分类'
		},
		'page_3' : {
			index : 2,
			name : '子分类',
			back : 'page_2',
			backname : ''
		},
		'page_4' : {
			index : 3,
			name : '东坡肉',
			back : 'page_3',
			backname : ''
		},
		'page_5' : {
			index : 4,
			name : '',
			back : '',
			backname : ''
		}
	},

	// ajax请求方法总汇
	ajaxMethod = {
		// 获取菜谱标签组code列表
		getCode : function(p, fn) {
			var str = JSON.stringify(p);
			code_fn = fn;
			Android_SDK.getCode_list(str);

			// Ajax('/code_list', p|| {'corp' : config.corp, 'act' :
			// 'code_list'} , fn , 'post');
			// Ajax('/v1/recipe/tag/code_list', p|| {'corp' : 'testcode', 'act'
			// : 'code_list'} , fn , 'post');
		},

		// 获取菜谱标签组详细列表
		getGroup_list : function(p, fn) {
			var str = JSON.stringify(p);
			group_fn = fn;
			Android_SDK.getGroup_list(str);
			// Ajax('/v1/recipe/tag/group_list', p|| {'corp' : config.corp,
			// 'act' : 'group_list', 'tag_groups':{
			// groups : [{
			// code : '0001',
			// ver : '1'
			// }]
			// }} , fn , 'post');
		},

		// 通过标签获取菜谱列表
		getFood : function(p, fn) {
			var json = {
				'corp' : config.corp,
				'act' : 'manual_get',
				'tags' : p
			};
			var str = JSON.stringify(json);
			food_fn = fn;
			Android_SDK.getFood_list(str);
			// Ajax('/v1/recipe/manual/get', p || {
			// 'corp' : config.corp,
			// 'act' : 'manual_get',
			// 'tags' : p || [{
			// code : '00001',
			// name : '1'
			// }]
			//
			// }, fn, 'post');
		}
	},

	header = Z('.header'),

	page_content = Z('#page_content'),

	// 处理顶部导航栏状态
	changeNav = function() {
		var pageData = pages[nowPage];
		header.find('h1').text(pageData.name);
		header.find('a').display(!!pageData.backname).text(pageData.backname);
	},

	setPage = function() {
		page_content.children().display(0);
		page_content.find('#' + nowPage).display(1);
	},

	setPagesData = function(obj) {
		var data = pages[nowPage];
		Z.extend(data, obj);
	},

	Tips = (function() {
		var tipsDom = Z('#ui_tips').display(0), timer = 2000, ui_modal = Z('#ui_modal'), timeHandler = null;

		return {
			set : function(msg, fn) {
				clearTimeout(timeHandler);
				if (!!msg === true) {
					timeHandler = setTimeout(function() {
								tipsDom.display(0);
								fn && fn();
							}, timer);
				}
				tipsDom.display(!!msg).find('div').text(msg);
			},

			setModal : function(msg) {
				ui_modal.display(!!msg);
			}
		}

	})(),

	getGroupByCode = function(obj) {
		var code = obj.code, groupData, group_list = _group_list
				|| Util.getStorage('group_list');
		for (var l = group_list.length, i = 0; i < l; i++) {
			if (code === group_list[i].code) {
				groupData = group_list[i];
				break;
			}
		}
		return groupData;
	},

	// 食材
	getMaterial = function(obj) {
		var trs = [];

		Z.each(obj, function(i, item) {
					trs.push(Util.parse(Tpl.get('foodMaterial'), item))
				});

		return trs.join('');
	},

	getSteps = function(obj) {
		var lis = [];

		Z.each(obj, function(i, item) {
					lis.push(Util.parse(Tpl.get('step'), Z.extend({
										step : i + 1
									}, item)));
				});

		return lis.join('');
	},

	nowRun = null, // 正在运行的任务对象
	foodObjId = 0,
	// 任务对象类
	runObj = function(runfood) {
		Z.extend(this, {
					data : runfood,
					id : ++foodObjId,
					step : 1,
					timer : null,
					timeout : 1000,
					isFinish : false,
					isStop : false,
					isDie : false,
					startTime : '',
					nowStepStart : '',
					nowStopTime : 0,
					procedure : runfood.procedure,
					allStep : runfood.procedure.length,
					// 当前步骤所需要时间(或者指定步骤)
					getStepTime : function(pro) {
						return (pro || this.procedure[this.step - 1])['formula'].time; // 秒
					},
					// 总共要花时间
					getAllTime : function() {
						var time = 0;
						Z.each(this.procedure, function(i, item) {
									time += item['formula'].time;
								});
						return time; // 秒
					},
					// 当前步骤剩余时间
					getStepResidueTime : function() {
						var st = this.getStepTime(), nowTime = +new Date;
						return st * 1000 - (nowTime - this.nowStepStart);

					},
					// 总剩余时间
					getResidueTime : function() {
						// debugger;
						var time = 0;
						for (var i = this.step; i < this.allStep; i++) {
							var procedure = this.procedure[i];
							time += this.getStepTime(procedure);
						}
						time *= 1000;
						// console.log('get:'+this.getStepResidueTime())
						time += this.getStepResidueTime();
						return time;
					},

					getNowStep : function(st) {
						return this.procedure[(st || this.step) - 1];
					},

					getNextStep : function() {
						if (this.step < this.allStep) {
							this.nowStepStart = +new Date;
							return this.step + 1;
						} else
							return false;
					},

					getNext : function(step) {
						var next = this.getNextStep();
						if (next) {
							return this.procedure[next - 1];
						} else {
							return false;
						}
					},

					// 开始一个步骤
					startProcedure : function(t) {
						this.nowStepStart = t || (+new Date);
						this.calculate();
						var self = this;
					},

					// 任务开始
					start : function() {
						this.startTime = +new Date;
						this.startProcedure(this.startTime);

					},

					getHour : function(t) {
						return t / 3600000 > 1;
					},

					getMun : function(t) {
						return t / 60000;
					},

					getMin : function(t) {
						return t / 1000;
					},

					showTimeHtml : function(time, type) {
						var html = [], temp = 'timeHour';
						if (type === 2) {
							temp = 'alltime';
						}
						var hour = this.getHour(t), ltime = time;
						if (hour > 1) {
							var t = Math.floor(hour);
							html.push(Util.parse(Tpl.get(temp), {
										t : t,
										name : '小时'
									}));

							time = time - t * 3600000;
							var mun = this.getMun(time);

							if (mun > 1) {
								t = Math.floor(mun);
								html.push(Util.parse(Tpl.get(temp), {
											t : t,
											name : '分钟'
										}));

								time = time - t * 60000;

								var min = this.getMin(time);

								html.push(Util.parse(Tpl.get(temp), {
											t : Math.floor(this.getMin(time)),
											name : '秒'
										}));
							} else {
								var min = this.getMin(time);

								html.push(Util.parse(Tpl.get(temp), {
											t : Math.floor(this.getMin(time)),
											name : '秒'
										}));
							}

						} else {

							var mun = this.getMun(time);

							if (mun > 1) {
								t = Math.floor(mun);
								html.push(Util.parse(Tpl.get(temp), {
											t : t,
											name : '分钟'
										}));

								time = time - t * 60000;

								var min = this.getMin(time);

								html.push(Util.parse(Tpl.get(temp), {
											t : Math.floor(this.getMin(time)),
											name : '秒'
										}));
							} else {
								var min = this.getMin(time);

								html.push(Util.parse(Tpl.get(temp), {
											t : Math.floor(this.getMin(time)),
											name : '秒'
										}));
							}
						}

						return html.join('');
					},

					calculate : function() {
						var self = this;
						this.timer = setInterval(function() {
									var t = self.getStepResidueTime();
									if (t <= 0) {
										self.clearTimer();
										F.fire('food-step-over' + self.id, {
													food : self,
													step : self.step
												});
									} else {
										self.setTimeState();
									}
								}, this.timeout);

					},

					setTimeState : function() {
						var t = self.getStepResidueTime();
						$('#allResidue').html('（总剩余时间：'
								+ self.showTimeHtml(self.getResidueTime(), 2)
								+ '）');
						$('#residue').html(self.showTimeHtml(t));
					},

					clearTimer : function() {
						clearInterval(this.timer);
					},

					stop : function(e) {
						this.nowStopTime = +new Date;
						this.clearTimer();
					},

					taskContinue : function() {
						this.nowStepStart = this.nowStepStart
								+ (+new Date - this.nowStopTime);
						this.calculate();
					},

					destroy : function() {
						this.clearTimer();
						this.isDie = true;
						F.actions.del('food-step-over' + this.id);
					}

				});
		var self = this;
		F.on('food-step-over' + self.id, function(opt) {
			// if(opt.id === self.id){
			var procedure = self.getNext();
			if (procedure) {
				self.step++;
				if (procedure.formula.procedure_mode === 'auto') {
					self.startProcedure();
				}
				F.fire('page-change', {
							type : 'go'
						});
			} else {// 完成任务
				F.fire('page-change', {
							type : 'go',
							finish : true
						});

			}
				// }
			});
	};

	F.actions.reg('group', function(e, obj) {// 获取子分类
				nowPage = 'page_2';

				var groupData = getGroupByCode(obj);

				setPagesData({
							name : groupData.name
						});

				F.fire('page-change', {
							type : 'go',
							code : obj.code,
							groupData : groupData
						});

			}).reg('cate', function(e, obj) {// 取分类下的菜单
				nowPage = 'page_3';
				var pcode = obj.pcode, groupData, code = obj.code, pcode = obj.pcode;
				var foods_list = _foods_list[code];
				var page = pages[nowPage];
				page.backname = header.find('h1').text();
				page.name = obj.target.text();
				if (foods_list) {
					F.fire('page-change', {
								type : 'go',
								code : obj.code,
								pcode : obj.pcode,
								foods_list : foods_list
							});
				} else {
					ajaxMethod.getFood([{
										code : code
									}], function(r) {
								r = Z.type(r) === 'string' ? JSON.parse(r) : r;
								foods_list = r.manuals;
								_foods_list[code] = foods_list;
								F.fire('page-change', {
											type : 'go',
											code : obj.code,
											pcode : obj.pcode,
											foods_list : foods_list
										});
							});
				}

			}).reg('return', function(e, obj) {// 返回
				var data = pages[nowPage];
				nowPage = data.back;
				F.fire('page-change', {
							type : 'return'
						})
			}).reg('food', function(e, obj) {// 查看菜谱详细
				nowPage = 'page_4';
				var page = pages[nowPage], index = obj.target.parent().index();
				var foods = _foods_list[obj.fcode][index];
				page.backname = '返回';
				page.name = foods.title;
				// console.log(foods)
				_now_food = foods;
				F.fire('page-change', {
							type : 'go',
							data : foods
						})
			}).reg('start', function(e, obj) {// 开始工作
				F.fire('boil-food-start', obj);
			}).reg('taskcontinue', function(e) {// 继续

				F.fire('page-change', {
							type : 'go',
							'auto' : true
						});
				nowRun.startProcedure();
			}).reg('taskstop', function(e) {// 是否中止
				// Tips.set('正在结束工作...', function(e){
				// nowPage = 'page_4';
				// F.fire('page-change', {
				// type : 'return'
				// });
				// })
				Tips.setModal('智能烤箱');
				nowRun.stop();
			}).reg('surestop', function() {// 中止
				Tips.setModal();
				Tips.set('正在结束工作...', function(e) {
							nowPage = 'page_4';
							F.fire('page-change', {
										type : 'return'
									})
						});

			}).reg('taskfinish', function(e) {// 完成任务
				nowPage = 'page_4';
				F.fire('page-change', {
							type : 'return'
						})
			}).reg('cancelmodal', function() {// 取消结束程序
				Tips.setModal();
				nowRun.taskContinue();
			});

	F.on('page-change', function(opt) {

		setPage();
		changeNav();

		// 根据路由各模块功能
		switch (nowPage) {

			case 'page_1' :// 首页模块

				var group_list = _group_list || Util.getStorage('group_list'),
				// 把取回来数据转化成需要的格式
				changeGroupCode = function(org) {

					return org.tag_groups;
				}, renderGroups = function(data) {
					var ulcon = [];
					Z.each(data, function(i, item) {
						var group_items = [];

						for (var len = item.tags.length, i = 0; i < len
								&& i < 6; i++) {
							var tag = item.tags[i];
							group_items.push('<span>'
									+ Util.parse(Tpl.get('groupItem'), Z
													.extend({
																pcode : item.code
															}, tag))
									+ '</span>');
						}

						ulcon.push(Util.parse(Tpl.get('groupLi'), Z.extend({
											lists : group_items.join('')
										}, item)));
					});

					return ulcon.join('');
				};

				if (!group_list) {
					ajaxMethod.getCode('', function(r) {
								alert(r);
								r = Z.type(r) === 'string' ? JSON.parse(r) : r;
								var groups = changeGroupCode(r);
								var Mgrouplist = groups.groups;
								for (var i = 0; i < Mgrouplist.length; i++) {
									// alert(JSON.stringify(groups[]));
									Mgrouplist[i].ver = 0;
								}
								ajaxMethod.getGroup_list({
											'corp' : "test",
											'act' : 'group_list',
											'tag_groups' : groups
										}, function(r) {
											r = Z.type(r) === 'string' ? JSON
													.parse(r) : r;
											group_list = r.tag_groups.groups;
											Util.setStorage('group_list',
													group_list);
											Z('#groups_list')
													.html(renderGroups(group_list));
										});
							})
				} else {
					_group_list = group_list;
					Z('#Groups_list').html(renderGroups(group_list));
				}

				break;

			case 'page_2' :// 子菜谱分类列表
				var groupData, code;
				if (opt.type === 'go') {
					groupData = opt.groupData;
					code = opt.code;
					var lis = [];

					Z.each(groupData.tags, function(i, tag) {
								lis.push('<li>'
										+ Util.parse(Tpl.get('groupItem'), Z
														.extend({
																	pcode : code
																}, tag))
										+ '</li>');
							});

					Z('#' + nowPage).find('ul').html(lis.join(''));

				}

				break;

			case 'page_3' :// 菜谱列表
				var foods_list, code, pcode, lis = [];

				if (opt.type === 'go') {
					code = opt.code;
					pcode = opt.pcode;
					foods_list = opt.foods_list;
					Z.each(foods_list, function(i, item) {
								lis.push(Util.parse(Tpl.get('food'), {
											desc : item.summary.desc,
											title : item.title,
											code : item.code,
											fcode : code,
											cover_img : item.cover_img
										}))
							});
					Z('#' + nowPage).find('ul').html(lis.join(''));
					// console.log('foods_list:',foods_list)
				} else {

				}
				break;

			case 'page_4' :// 菜谱详细
				if (opt.type === 'go') {
					var item = opt.data;
					Z('#' + nowPage).html(Util.parse(Tpl.get('foodDetail'), {
								desc : item.summary.desc,
								title : item.title,
								code : item.code,
								commit : item.commit,
								material : getMaterial(item.summary.materials),
								steps : getSteps(item.procedure),
								cover_img : item.cover_img
							}));
				}
				break;
			case 'page_5' :// 程序运行
				var btnHtml = '', pro = nowRun.getNowStep();
				if (opt.finish) {
					btnHtml = '<a href="#" class="btn btn-primary btn-block" rel="e:taskfinish">完成</a>';
				} else if (pro.formula.procedure_mode === 'auto' || opt.auto) {
					btnHtml = '<a href="#" rel="e:taskstop" class="btn btn-primary btn-block">结束</a>';
				} else if (pro.formula.procedure_mode === 'manual') {
					btnHtml = '<a href="#" rel="e:taskcontinue" class="btn btn-primary btn-block">继续</a>';
				}

				Z('#' + nowPage).html(Util.parse(Tpl.get('runStep'), {
							desc : _now_food.summary.desc,
							title : _now_food.title,
							code : _now_food.code,
							btn : btnHtml,
							stepNumber : nowRun.step + '/' + nowRun.allStep,
							temperature : pro.formula.temperature,
							isAuto : pro.formula.procedure_mode,
							cover_img : _now_food.cover_img
						}));
				nowRun.setTimeState();
				if (pro.formula.procedure_mode === 'manual' && !opt.auto) {
					$('#residue').remove();
					$('.countdown h3').text('等待继续');
				}
				// if(opt.finish){
				$('#taskSuccess').display(!!opt.finish);
				$('#runing').display(!opt.finish);
				// }
				break;
		}

	}).on('boil-food-start', function(opt) {
				Tips.set('正在启动食谱', function() {
							nowPage = 'page_5';
							nowRun = new runObj(_now_food);
							nowRun.start();
							var page = pages[nowPage];
							page.name = _now_food.title;
							F.fire('page-change', {
										type : ''
									});

						});
			});

	// html模板数据
	var Tpl = {

		get : function(name) {
			return this[name];
		},

		// 首页列表
		'groupLi' : [
				'<li>',
				'<a href="list.html?code={.code}" rel="e:group,code:{.code}"><img src="{.img}" alt=""><span>{.name}</span></a>',
				'<div class="subs">{.lists}</div>', '</li>'].join(''),
		// 子分类
		'groupItem' : '<a href="menu-list.html?code={.code}" rel="e:cate,code:{.code},pcode:{.pcode}">{.name}</a>',

		// 菜普
		'food' : [
				'<li><a href="menu-details.html" rel="e:food,code:{.code},fcode:{.fcode}">',
				'<div class="thumb"><img src="http://42.121.122.228:8680/app_img_dw?path={.cover_img}" alt=""></div>',
				'<div class="info">', '<h3>{.title}</h3>', '<p>{.desc}</p>',
				'</div></a></li>'].join(''),
		// 菜普详细
		'foodDetail' : [
				'<div class="figure"><img src="http://42.121.122.228:8680/app_img_dw?path={.cover_img}" alt=""></div>',
				'<article><h1> {.title}</h1>',
				'<div class="author">{.creator}</div>',
				'<div class="date">{.createtime}发布</div>',
				'<div class="summary">',
				'<p>{.desc}</p>',
				'<p class="hidden">{.desc}</p>',
				'<div class="view-all" rel="e:readAll">查看全部</div>',
				'</div>',
				'<div class="sec">',
				'<div class="sec-hd">食材</div>',
				'<div class="sec-bd">',
				'<div class="table">',
				'<table>',
				'<tbody>',
				'{.material}',
				'</tbody>',
				'</table>',
				'</div>',
				'</div>',
				'</div>',
				'<div class="sec">',
				'    <div class="sec-hd">步骤</div>',
				'    <div class="sec-bd">',
				'       <ul class="steps">{.steps}</ul>',
				'</div>',
				'</div>',
				'<div class="sec">',
				'<div class="sec-hd">备注</div>',
				'<div class="sec-bd">',
				'<p>{.desc}</p>',
				'</div>',
				'</div>',
				'<div class="actions"><a href="#" class="btn btn-primary btn-block" rel="e:start,code:{.code}">一键启动</a></div>',
				'</article>'].join(''),

		// 食材
		'foodMaterial' : ['<tr>', '<th>{.name}</th>', '<td>{.quantity}</td>',
				'</tr>'].join(''),
		// 步骤
		'step' : [
				'<li><div class="thumb"><img src="http://42.121.122.228:8680/app_img_dw?path={.img}" alt="">',
				'</div>', '<div class="info"><span>{.step}</span>',
				'<p>{.decs}</p>', '</div>', '</li>'].join(''),
		// 运行步骤
		'runStep' : [
				'<div class="figure"><img src="http://42.121.122.228:8680/app_img_dw?path={.cover_img}" alt="">',
				'<div class="step-info"><span>{.stepNumber}</span>',
				'<p> 烤箱设定温度{.temperature}℃</p>',
				'</div>',
				'</div>',
				'<div class="msg msg-success hidden" id="taskSuccess">任务完成</div>',
				'<div class="countdown" id="runing">', '<h3>剩余时间</h3>',
				'<div class="clock" id="residue"></div>',
				'<div class="hints" id="allResidue"></div>', '</div>',
				'<div class="actions">{.btn}</div>'].join(''),

		'timeHour' : '<strong>{.t}</strong><span>{.name}</span>',

		'alltime' : '{.t}{.name}'
	}

	var config = {
		user : 'xxxx-1',
		corp : 'xxxx-2'
	};
	var nowPage = 'page_1';
	$(function() {

				F.event();

				F.fire('page-change');

				// 获取配置信息，在页面底部写入，详细见index.html底部
				var pd = F.PD.get();
				Z.extend(config, pd);

			});

})(Zepto, window, Food);
