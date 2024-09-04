"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[5754],{4863:(e,t,n)=>{n.d(t,{Z:()=>r});var a=n(7462),l=n(7294),s=n(5671),o=n(3746);const r=e=>{let{children:t,onChange:n,language:r}=e;const i=(0,l.useRef)(null),[c,u]=(0,l.useState)(t),p=(0,l.useCallback)((e=>{const t=e.slice(0,-1);u(t),n(t)}),[]);return(0,s.Y)(i,p,{indentation:2}),l.createElement(o.ZP,(0,a.Z)({},o.lG,{code:c,language:r}),(e=>{let{className:t,style:n,tokens:a,getTokenProps:s}=e;return l.createElement("pre",{className:t,style:n,ref:i},a.map(((e,t)=>l.createElement(l.Fragment,{key:t},e.filter((e=>!e.empty)).map(((e,t)=>l.createElement("span",s({token:e,key:t})))),"\n"))))}))}},4294:(e,t,n)=>{n.d(t,{Z:()=>c});var a=n(7294),l=n(9669),s=n.n(l),o=n(4863),r=n(9286),i=n(2389);const c=e=>{let{defaultExpression:t,feelContext:n,metadata:l,onResultCallback:c,onErrorCallback:u}=e;const p=function(){let e={};if((0,i.Z)()){const t=window.location.search,n=new URLSearchParams(t);n.has("expression")&&(e.expression=N(n.get("expression"))),n.has("context")&&(e.context=N(n.get("context")))}return e}();let d=p.context??n;d&&(d=JSON.stringify(JSON.parse(d),null,2));const[m,h]=a.useState(p.expression??t),[g,f]=a.useState(d),[v,k]=a.useState("<click 'Evaluate' to see the result of the expression>"),[x,b]=a.useState(null),[E,y]=a.useState(null),w=/^.+(?<line>\d+):(?<position>\d+).+$/gm,C=/^.+at position (?<position>\d+)$/gm;function S(e,t){k(null),b(e),y(t),u&&u(e)}function Z(e){return btoa(e)}function N(e){return atob(e)}return a.createElement("div",null,a.createElement("h2",null,"Expression"),a.createElement(o.Z,{onChange:h,language:"js"},m),n&&a.createElement("div",null,a.createElement("h2",null,"Context"),a.createElement("i",null,"A JSON document that is used to resolve ",a.createElement("strong",null,"variables")," ","in the expression."),a.createElement(o.Z,{onChange:f,language:"json"},g)),a.createElement("button",{onClick:function(){try{k("<evaluating the expression...>"),y(null);!function(e){s().post("https://feel.upgradingdave.com/api/v1/feel/evaluate",{expression:m,context:e,metadata:{...l}},{headers:{accept:"*/*","content-type":"application/json"}}).then((e=>{var t;if(null!=e&&null!=(t=e.data)&&t.error){var n,a;const t=e.data.error,l=w.exec(t);S({message:t,line:null==l||null==(n=l.groups)?void 0:n.line,position:null==l||null==(a=l.groups)?void 0:a.position},e.data.warnings)}else!function(e){b(null);const t=JSON.stringify(e.result);k(t),e.warnings.length>=1&&y(e.warnings);c&&c(t)}(e.data)}))}(n&&0!==g.trim().length?JSON.parse(g):{})}catch(t){var e;const n=C.exec(t.message);S({message:`failed to parse context: ${t.message}`,position:null==n||null==(e=n.groups)?void 0:e.position})}},className:"button button--primary button--lg"},"Evaluate"),a.createElement("button",{onClick:function(){navigator.clipboard.writeText(function(){const e=window.location.href.split("?")[0],t=window.location.search,n=new URLSearchParams(t);return n.set("expression",Z(m)),g&&n.set("context",Z(g)),n.set("expression-type","expression"),e+"?"+n}())},className:"button button--secondary button--lg",title:"Copy an URL to the clipboard for sharing the expression",style:{"margin-left":"10px"}},"Share"),a.createElement("br",null),a.createElement("br",null),a.createElement("h2",null,"Result"),a.createElement(r.Z,{title:(()=>{const e=null!=x&&x.line?` on line ${x.line}`:"",t=null!=x&&x.position?` at position ${x.position}`:"";return x&&`Error${e}${t}`})(),language:"json"},v||(null==x?void 0:x.message)),a.createElement("br",null),a.createElement("h2",null,"Warnings"),a.createElement(r.Z,null,(null==E?void 0:E.map(((e,t)=>a.createElement("li",{key:t},"[",e.type,"] ",e.message))))||"<none>"))}},9928:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>p,contentTitle:()=>c,default:()=>h,frontMatter:()=>i,metadata:()=>u,toc:()=>d});var a=n(7462),l=(n(7294),n(3905)),s=n(4294),o=n(6518),r=n.n(o);const i={id:"chapter-1",title:"Chapter 1"},c=void 0,u={unversionedId:"learn/challenge/chapter-1",id:"version-1.18/learn/challenge/chapter-1",title:"Chapter 1",description:"Let's start our quest. FEEL allows you to use basic",source:"@site/versioned_docs/version-1.18/learn/challenge/chapter-1.mdx",sourceDirName:"learn/challenge",slug:"/learn/challenge/chapter-1",permalink:"/feel-scala/docs/learn/challenge/chapter-1",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/main/docs/versioned_docs/version-1.18/learn/challenge/chapter-1.mdx",tags:[],version:"1.18",frontMatter:{id:"chapter-1",title:"Chapter 1"},sidebar:"Learn",previous:{title:"The quest begins",permalink:"/feel-scala/docs/learn/challenge/"},next:{title:"Chapter 2",permalink:"/feel-scala/docs/learn/challenge/chapter-2"}},p={},d=[],m={toc:d};function h(e){let{components:t,...n}=e;return(0,l.kt)("wrapper",(0,a.Z)({},m,n,{components:t,mdxType:"MDXLayout"}),(0,l.kt)("p",null,"Let's start our quest. \ud83d\udea9 FEEL allows you to use basic\n",(0,l.kt)("a",{parentName:"p",href:"https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-numeric-expressions/"},"numeric calculations")," like addition,\nsubtraction and multiplication to name a few. Our friend arrived in Spain by boat, specifically\nCadiz. The goal is to reach Pamplona (which is 1,030.8 kms away)."),(0,l.kt)("p",null,"As part of the quest, Zee received magical items \u2728 and decided to use The Boots of Hermes\n\ud83d\udc5e, which give its wearer a speed of 48.2 kms/hour."),(0,l.kt)("p",null,"Using numeric operators, how many hours would it take to get there? Consider resting for 30 minutes\nevery 5 hours. Let's also round up the number for total resting time by using a\n",(0,l.kt)("a",{parentName:"p",href:"https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-numeric/"},"numeric function"),"."),(0,l.kt)(s.Z,{defaultExpression:r()`
      // change formula considering resting time plus total time
      round up(distance / speed, 0)`,feelContext:'{"distance": 1030.8, "speed": 48.2, "restInHrs": 0.5, "restInterval": 5}',metadata:{page:"challenge-1"},mdxType:"LiveFeel"}),(0,l.kt)("details",null,(0,l.kt)("summary",null,"Solution"),(0,l.kt)("div",null,(0,l.kt)("div",null,"It would take Zee 24 hours to complete the trip."),(0,l.kt)("br",null),(0,l.kt)("pre",{title:"Expression"},"round up(restInHrs * (distance / speed) / restInterval + distance / speed, 0)"))))}h.isMDXComponent=!0}}]);