"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[2584],{4863:(e,t,n)=>{n.d(t,{Z:()=>s});var a=n(7462),l=n(7294),o=n(5671),r=n(3746);const s=e=>{let{children:t,onChange:n,language:s}=e;const i=(0,l.useRef)(null),[u,c]=(0,l.useState)(t),p=(0,l.useCallback)((e=>{const t=e.slice(0,-1);c(t),n(t)}),[]);return(0,o.Y)(i,p,{indentation:2}),l.createElement(r.ZP,(0,a.Z)({},r.lG,{code:u,language:s}),(e=>{let{className:t,style:n,tokens:a,getTokenProps:o}=e;return l.createElement("pre",{className:t,style:n,ref:i},a.map(((e,t)=>l.createElement(l.Fragment,{key:t},e.filter((e=>!e.empty)).map(((e,t)=>l.createElement("span",o({token:e,key:t})))),"\n"))))}))}},4294:(e,t,n)=>{n.d(t,{Z:()=>i});var a=n(7294),l=n(9669),o=n.n(l),r=n(4863),s=n(9537);const i=e=>{let{defaultExpression:t,feelContext:n,metadata:l}=e;n&&(n=JSON.stringify(JSON.parse(n),null,2));const[i,u]=a.useState(t),[c,p]=a.useState(n),[d,m]=a.useState("<click 'Evaluate' to see the result of the expression>"),[g,h]=a.useState(null),f=/^.+(?<line>\d+):(?<position>\d+).+$/gm;return a.createElement("div",null,a.createElement("h2",null,"Expression"),a.createElement(r.Z,{onChange:u,language:"js"},i),n&&a.createElement("div",null,a.createElement("h2",null,"Context"),a.createElement("i",null,"A JSON document that is used to resolve ",a.createElement("strong",null,"variables")," ","in the expression."),a.createElement(r.Z,{onChange:p,language:"json"},c)),a.createElement("button",{onClick:function(){const e=n?JSON.parse(c):{};o().post("https://feel.upgradingdave.com/process/start",{expression:i,context:e,metadata:{...l}},{headers:{accept:"*/*","content-type":"application/json"}}).then((e=>{var t,n;if(null!=e&&null!=(t=e.data)&&t.result)h(null),m(JSON.stringify(e.data.result));else if(null!=e&&null!=(n=e.data)&&n.error){var a,l;const t=e.data.error,n=f.exec(t);m(null),h({message:t,line:null==n||null==(a=n.groups)?void 0:a.line,position:null==n||null==(l=n.groups)?void 0:l.position})}}))},className:"button button--primary button--lg"},"Evaluate"),a.createElement("br",null),a.createElement("br",null),a.createElement("h2",null,"Result"),a.createElement(s.Z,{title:(()=>{const e=null!=g&&g.line?" on line "+g.line:"",t=null!=g&&g.position?" at position "+g.position:"";return g&&"Error"+e+t})(),language:"json"},d||(null==g?void 0:g.message)))}},8043:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>m,contentTitle:()=>p,default:()=>f,frontMatter:()=>c,metadata:()=>d,toc:()=>g});var a,l=n(7462),o=n(1880),r=(n(7294),n(3905)),s=n(4294),i=n(6518),u=n.n(i);const c={id:"tutorial-3-1",title:"3.1 Fifth Stop: Temporal expressions"},p=void 0,d={unversionedId:"tutorial/tutorial-3-1",id:"tutorial/tutorial-3-1",title:"3.1 Fifth Stop: Temporal expressions",description:"The next stop would put Zee in Cologne, as the journey continued there was an important question to answer: would Zee get in time for the conference?",source:"@site/docs/tutorial/tutorial-3-1.mdx",sourceDirName:"tutorial",slug:"/tutorial/tutorial-3-1",permalink:"/feel-scala/docs/tutorial/tutorial-3-1",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/master/docs/docs/tutorial/tutorial-3-1.mdx",tags:[],version:"current",frontMatter:{id:"tutorial-3-1",title:"3.1 Fifth Stop: Temporal expressions"},sidebar:"Tutorial",previous:{title:"2.1 Fourth Stop: String expressions",permalink:"/feel-scala/docs/tutorial/tutorial-2-1"},next:{title:"4.1 Final Stop: Lists expressions",permalink:"/feel-scala/docs/tutorial/tutorial-4-1"}},m={},g=[],h={toc:g};function f(e){let{components:t,...n}=e;return(0,r.kt)("wrapper",(0,l.Z)({},h,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"The next stop would put Zee in Cologne, as the journey continued there was an important question to answer: would Zee get in time for the conference?"),(0,r.kt)("p",null,"The trip started on September 15th, 2022, and since the journey began, around 200 hours had passed. CamundaCon will start on October 5th, 2022. Consider the distance between Cologne and Berlin is 579 kms and as we already know, a walking average of 5 km/h."),(0,r.kt)("p",null,"Let's use numeric and temporal expressions to answer the question:"),(0,r.kt)(s.Z,{defaultExpression:u()(a||(a=(0,o.Z)(['\n    // Temporal function and period of time calculation\n    date(startingDate) + duration("PT" + string(round up(200 + 579/5,0)) + "H")']))),feelContext:'{"startingDate": "2022-09-15", "targetDate": "2022-10-05"}',metadata:{page:"tutorial-3-1"},mdxType:"LiveFeel"}),(0,r.kt)("p",null,"It looks like Zee will make it in time and arrive in Berlin on September 28th!!!"))}f.isMDXComponent=!0},6518:e=>{e.exports=function(e){var t=void 0;t="string"==typeof e?[e]:e.raw;for(var n="",a=0;a<t.length;a++)n+=t[a].replace(/\\\n[ \t]*/g,"").replace(/\\`/g,"`"),a<(arguments.length<=1?0:arguments.length-1)&&(n+=arguments.length<=a+1?void 0:arguments[a+1]);var l=n.split("\n"),o=null;return l.forEach((function(e){var t=e.match(/^(\s+)\S+/);if(t){var n=t[1].length;o=o?Math.min(o,n):n}})),null!==o&&(n=l.map((function(e){return" "===e[0]?e.slice(o):e})).join("\n")),(n=n.trim()).replace(/\\n/g,"\n")}},1880:(e,t,n)=>{function a(e,t){return t||(t=e.slice(0)),e.raw=t,e}n.d(t,{Z:()=>a})}}]);