package com.meng;
import com.meng.groupMsgProcess.*;
import com.meng.tools.*;

public class Dice extends BaseModule{
	
	@Override
	public BaseModule load() {
		enable = true;
		return this;
	}
	
	String strip(String origin) {
		boolean flag = true;
		while (flag) {
			flag = false;
			if (origin[0] == '!' || origin[0] == '.') {
				origin.erase(origin.begin());
				flag = true;
			} else if (origin.substring(0, 2) == "！" || origin.substring(0, 2) == "。") {
				origin.erase(origin.begin());
				origin.erase(origin.begin());
				flag = true;
			}
		}
		return origin;
	}
	
	@Override
	protected boolean processMsg(long fromGroup, long fromQQ, String msg, int msgId) {
		while (msg.charAt(0)==' '){
			msg=msg.substring(0,1);
		}
		String strAt = "[CQ:at,qq=" + Autoreply.CQ.getLoginQQ() + "]";
		if(Tools.CQ.isAtme(msg)){
			msg=msg.substring(strAt.length());
		}else if(Autoreply.CC.getAt(msg)!=-1){
			return false;
		}
		if (dice_msg.msg[0] != '.'){
			return fasle;
			}
		int intMsgCnt = 1;
		while (msg.charAt(intMsgCnt++)==' ');
		String strNickName = getName(dice_msg.qq_id, dice_msg.group_id);
		String strLowerMessage = msg.toLowerCase();
		if (strLowerMessage.substring(intMsgCnt, 3) .equals("bot")){
			intMsgCnt += 3;
		while (strLowerMessage.charAt(intMsgCnt++)==' ');
		String Command;
		while (intMsgCnt != strLowerMessage.length() && !Character.isDigit(strLowerMessage.charAt(intMsgCnt)) && !strLowerMessage.charAt(intMsgCnt)==' '){
				Command += strLowerMessage[intMsgCnt];
				++intMsgCnt;
			}
		while (strLowerMessage.charAt(intMsgCnt++)==' ');
			String QQNum = strLowerMessage.substring(intMsgCnt, msg.indexOf(" ", intMsgCnt) - intMsgCnt);
			if (Command.equals("on")){
				if (QQNum.equals("") || QQNum.equals(String.valueOf(Autoreply.CQ.getLoginQQ())) || (QQNum.length() == 4 && QQNum == String.valueOf(getLoginQQ() % 10000))){
						if (getGroupMemberInfo(dice_msg.group_id, dice_msg.qq_id).permissions >= 2){
							if (DisabledGroup.count(dice_msg.group_id)){
								DisabledGroup.erase(dice_msg.group_id);
								dice_msg.Reply(GlobalMsg["strSuccessfullyEnabledNotice"]);
							}else{
								dice_msg.Reply(GlobalMsg["strAlreadyEnabledErr"]);
							}
						}else{
							dice_msg.Reply(GlobalMsg["strPermissionDeniedErr"]);
						}
				}
			}else if (Command.equals("off")){
		if (QQNum.isEmpty() || QQNum.equals(String.valueOf(Autoreply.CQ.getLoginQQ())) || (QQNum.length() == 4 && QQNum == String.valueOf(getLoginQQ() % 10000))){
							if (getGroupMemberInfo(dice_msg.group_id, dice_msg.qq_id).permissions >= 2){
							if (!DisabledGroup.count(dice_msg.group_id)){
								DisabledGroup.insert(dice_msg.group_id);
								dice_msg.Reply(GlobalMsg["strSuccessfullyDisabledNotice"]);
							}else{
								dice_msg.Reply(GlobalMsg["strAlreadyDisabledErr"]);
							}
						}else{
							dice_msg.Reply(GlobalMsg["strPermissionDeniedErr"]);
						}
				}
			}else{
		if (QQNum.isEmpty() || QQNum.equals(String.valueOf(Autoreply.CQ.getLoginQQ())) || (QQNum.length() == 4 && QQNum == String.valueOf(getLoginQQ() % 10000))){
		dice_msg.Reply(Dice_Full_Ver);
				}
			}
		}else if (strLowerMessage.substring(intMsgCnt, 7).equals("dismiss")){
			intMsgCnt += 7;
		while (strLowerMessage.charAt(intMsgCnt++)==' ');
		String QQNum;
			while (Character.isDigit(strLowerMessage.charAt(intMsgCnt))){
				QQNum += strLowerMessage[intMsgCnt];
				++intMsgCnt;
			}
		if (QQNum.equals("") || QQNum.equals(String.valueOf(Autoreply.CQ.getLoginQQ())) || (QQNum.length() == 4 && QQNum == String.valueOf(getLoginQQ() % 10000))){
							if (getGroupMemberInfo(dice_msg.group_id, dice_msg.qq_id).permissions >= 2){
						setGroupLeave(dice_msg.group_id);
					}else{
						dice_msg.Reply(GlobalMsg["strPermissionDeniedErr"]);
					}
			}
		}else if (strLowerMessage.substring(intMsgCnt, 4).equals("help")){
			intMsgCnt += 4;
			while (strLowerMessage.charAt(intMsgCnt++)==' ');
			final String strAction = strLowerMessage.substring(intMsgCnt);
			if (strAction.equals("on")){
					if (getGroupMemberInfo(dice_msg.group_id, dice_msg.qq_id).permissions >= 2){
						if (DisabledHELPGroup.count(dice_msg.group_id)){
							DisabledHELPGroup.erase(dice_msg.group_id);
							dice_msg.Reply(GlobalMsg["strHelpCommandSuccessfullyEnabledNotice"]);
						}else{
							dice_msg.Reply(GlobalMsg["strHelpCommandAlreadyEnabledErr"]);
						}
					}else{
						dice_msg.Reply(GlobalMsg["strPermissionDeniedErr"]);
					}
					return;
			}
			if (strAction.equals("off")){
						if (getGroupMemberInfo(dice_msg.group_id, dice_msg.qq_id).permissions >= 2){
						if (!DisabledHELPGroup.count(dice_msg.group_id)){
							DisabledHELPGroup.insert(dice_msg.group_id);
							dice_msg.Reply(GlobalMsg["strHelpCommandSuccessfullyDisabledNotice"]);
						}else{
							dice_msg.Reply(GlobalMsg["strHelpCommandAlreadyDisabledErr"]);
						}
					}else{
						dice_msg.Reply(GlobalMsg["strPermissionDeniedErr"]);
					}
				return;
			}
			dice_msg.Reply(GlobalMsg["strHelpMsg"]);
		}else if (strLowerMessage.substring(intMsgCnt, 7).equals("welcome")){
					intMsgCnt += 7;
			while (strLowerMessage.charAt(intMsgCnt++)==' ');
			if (getGroupMemberInfo(dice_msg.group_id, dice_msg.qq_id).permissions >= 2){
				String strWelcomeMsg = msg.substring(intMsgCnt);
				if (strWelcomeMsg.isEmpty()){
					if (WelcomeMsg.count(dice_msg.group_id)){
						WelcomeMsg.erase(dice_msg.group_id);
						dice_msg.Reply(GlobalMsg["strWelcomeMsgClearedNotice"]);
					}else{
						dice_msg.Reply(GlobalMsg["strWelcomeMsgIsEmptyErr"]);
					}
				}else{
					WelcomeMsg[dice_msg.group_id] = strWelcomeMsg;
					dice_msg.Reply(GlobalMsg["strWelcomeMsgUpdatedNotice"]);
				}
			}else{
				dice_msg.Reply(GlobalMsg["strPermissionDeniedErr"]);
			}
		}else if (strLowerMessage.substring(intMsgCnt, 2).equals("st")){
			intMsgCnt += 2;
			while (strLowerMessage.charAt(intMsgCnt++)==' ');
			if (intMsgCnt == strLowerMessage.length()){
				dice_msg.Reply(GlobalMsg["strStErr"]);
				return;
			}
			if (strLowerMessage.substring(intMsgCnt, 3).equals("clr")){
				if (CharacterProp.count(SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id))){
					CharacterProp.erase(SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id));
				}
				dice_msg.Reply(GlobalMsg["strPropCleared"]);
				return;
			}
			if (strLowerMessage.substring(intMsgCnt, 3).equals("del")){
				intMsgCnt += 3;
				while (strLowerMessage.charAt(intMsgCnt++)==' ');
				String strSkillName;
				while (intMsgCnt != strLowerMessage.length() && !isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) && !(strLowerMessage[intMsgCnt] == '|')){
					strSkillName += strLowerMessage[intMsgCnt];
					intMsgCnt++;
				}
					if (SkillNameReplace.count(strSkillName))strSkillName = SkillNameReplace[strSkillName];
					if (CharacterProp.count(SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)) && CharacterProp[SourceType(
						dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)].count(strSkillName)){
						CharacterProp[SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)].erase(strSkillName);
						dice_msg.Reply(GlobalMsg["strPropDeleted"]);
					}else{
						dice_msg.Reply(GlobalMsg["strPropNotFound"]);
					}
					return;
			}
			if (strLowerMessage.substring(intMsgCnt, 4) == "show"){
				intMsgCnt += 4;
				while (strLowerMessage.charAt(intMsgCnt++)==' ');
			String strSkillName;
				while (intMsgCnt != strLowerMessage.length() && !isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) && !(strLowerMessage[intMsgCnt] == '|')){
					strSkillName += strLowerMessage[intMsgCnt];
					intMsgCnt++;
				}
					if (SkillNameReplace.count(strSkillName))strSkillName = SkillNameReplace[strSkillName];
					if (CharacterProp.count(SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)) && CharacterProp[SourceType(
						dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)].count(strSkillName)){
						dice_msg.Reply(format(GlobalMsg["strProp"], 
							{
								{ "nick", strNickName },
								{ "prop_name", strSkillName },
								{ "prop_value", to_string(CharacterProp[SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)][strSkillName]) }
							}
						));
					}else if (SkillDefaultVal.count(strSkillName)){
						dice_msg.Reply(format(GlobalMsg["strProp"],
							{
								{ "nick", strNickName },
								{ "prop_name", strSkillName },
								{ "prop_value", to_string(SkillDefaultVal[strSkillName]) }
							}
						));
					}else{
						dice_msg.Reply(GlobalMsg["strPropNotFound"]);
					}
					return;
			}
			boolean booleanError = false;
			while (intMsgCnt != strLowerMessage.length()){
				String strSkillName;
				while (intMsgCnt != strLowerMessage.length() && !isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) && !isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) && strLowerMessage[intMsgCnt] != '=' && strLowerMessage[intMsgCnt]!= ':'){
					strSkillName += strLowerMessage[intMsgCnt];
					intMsgCnt++;
				}
				if (SkillNameReplace.count(strSkillName))strSkillName = SkillNameReplace[strSkillName];
				while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) || strLowerMessage[intMsgCnt] == '=' || strLowerMessage[intMsgCnt] == ':')intMsgCnt++;
				String strSkillVal;
				while (isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt]))){
					strSkillVal += strLowerMessage[intMsgCnt];
					intMsgCnt++;
				}
				if (strSkillName.empty() || strSkillVal.empty() || strSkillVal.length() > 3){
					booleanError = true;
					break;
				}
				CharacterProp[SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)][strSkillName] = stoi(strSkillVal);
				while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) || strLowerMessage[intMsgCnt] == '|')intMsgCnt++;
			}
			if (booleanError){
				dice_msg.Reply(GlobalMsg["strPropErr"]);
			} else {
				dice_msg.Reply(GlobalMsg["strSetPropSuccess"]);
			}
		}else if (strLowerMessage.substring(intMsgCnt, 2) == "ri"){
			if (dice_msg.msg_type == Dice::MsgType::Private){
				dice_msg.Reply(GlobalMsg["strCommandNotAvailableErr"]);
				return;
			}
			intMsgCnt += 2;
				while (strLowerMessage.charAt(intMsgCnt++)==' ');
String strinit = "D20";
			if (strLowerMessage[intMsgCnt] == '+' || strLowerMessage[intMsgCnt] == '-'){
				strinit += strLowerMessage[intMsgCnt];
				intMsgCnt++;
				while (strLowerMessage.charAt(intMsgCnt++)==' ');
} else if (isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt])))
				strinit += '+';
			while (isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt]))){
				strinit += strLowerMessage[intMsgCnt];
				intMsgCnt++;
			}
			while (strLowerMessage.charAt(intMsgCnt++)==' ');
			String strname = msg.substring(intMsgCnt).isEmpty()?strNickName:strip(strname);
			RD initdice(strinit);
		 int intFirstTimeRes = initdice.Roll();
			if (intFirstTimeRes == Value_Err){
				dice_msg.Reply(GlobalMsg["strValueErr"]);
				return;
			}
			if (intFirstTimeRes == Input_Err){
				dice_msg.Reply(GlobalMsg["strInputErr"]);
				return;
			}
			if (intFirstTimeRes == ZeroDice_Err){
				dice_msg.Reply(GlobalMsg["strZeroDiceErr"]);
				return;
			}
			if (intFirstTimeRes == ZeroType_Err){
				dice_msg.Reply(GlobalMsg["strZeroTypeErr"]);
				return;
			}
			if (intFirstTimeRes == DiceTooBig_Err){
				dice_msg.Reply(GlobalMsg["strDiceTooBigErr"]);
				return;
			}
			if (intFirstTimeRes == TypeTooBig_Err){
				dice_msg.Reply(GlobalMsg["strTypeTooBigErr"]);
				return;
			}
			if (intFirstTimeRes == AddDiceVal_Err){
				dice_msg.Reply(GlobalMsg["strAddDiceValErr"]);
				return;
			}
			if (intFirstTimeRes != 0){
				dice_msg.Reply(GlobalMsg["strUnknownErr"]);
				return;
			}
			ilInitList->insert(dice_msg.group_id, initdice.intTotal, strname);
			dice_msg.Reply(format(GlobalMsg["strInitReplyMsg"], {
				{ "nick", strname },
				{ "dice_res", strinit + '=' + to_string(initdice.intTotal) }
			}));
		}else if (strLowerMessage.substring(intMsgCnt, 4) == "init"){
			if (dice_msg.msg_type == Dice::MsgType::Private){
				dice_msg.Reply(GlobalMsg["strCommandNotAvailableErr"]);
				return;
			}
			intMsgCnt += 4;
			String strReply;
			while (strLowerMessage.charAt(intMsgCnt++)==' ');
if (strLowerMessage.substring(intMsgCnt, 3) == "clr"){
				if (ilInitList->clear(dice_msg.group_id))
					strReply = GlobalMsg["strInitListClearedNotice"];
				else
					strReply = GlobalMsg["strInitListIsEmptyErr"];
				dice_msg.Reply(strReply);
				return;
			}
			ilInitList->show(dice_msg.group_id, strReply);
			dice_msg.Reply(strReply);
		}else if (strLowerMessage[intMsgCnt] == 'w'){
			intMsgCnt++;
			boolean booleanDetail = false;
			if (strLowerMessage[intMsgCnt] == 'w'){
				intMsgCnt++;
				booleanDetail = true;
			}
			boolean isHidden = false;
			if (strLowerMessage[intMsgCnt] == 'h'){
				isHidden = true;
				intMsgCnt += 1;
			}
			while (msg.charAt(intMsgCnt++)==' ');
			int intTmpMsgCnt;
			for (intTmpMsgCnt = intMsgCnt; intTmpMsgCnt != dice_msg.msg.length() && dice_msg.msg[intTmpMsgCnt] != ' ';
				intTmpMsgCnt++){
				if (!isdigit(static_cast<unsigned char>(strLowerMessage[intTmpMsgCnt])) && strLowerMessage[intTmpMsgCnt] != 'd' && strLowerMessage[
					intTmpMsgCnt] != 'k' && strLowerMessage[intTmpMsgCnt] != 'p' && strLowerMessage[intTmpMsgCnt] != 'b'
						&&
						strLowerMessage[intTmpMsgCnt] != 'f' && strLowerMessage[intTmpMsgCnt] != '+' && strLowerMessage[
							intTmpMsgCnt
						] != '-' && strLowerMessage[intTmpMsgCnt] != '#' && strLowerMessage[intTmpMsgCnt] != 'a' && strLowerMessage[intTmpMsgCnt] != 'x' && strLowerMessage[intTmpMsgCnt] != '*'){
					break;
				}
			}
			String strMainDice = strLowerMessage.substring(intMsgCnt, intTmpMsgCnt - intMsgCnt);
			while (msg.charAt(intTmpMsgCnt++)==' ');
			String strReason = msg.substring(intTmpMsgCnt);
			int intTurnCnt = 1;
			if (strMainDice.find('#') != string::npos){
				String strTurnCnt = strMainDice.substring(0, strMainDice.find('#'));
				if (strTurnCnt.empty())
					strTurnCnt = "1";
				strMainDice = strMainDice.substring(strMainDice.find('#') + 1);
				const int intDefaultDice = DefaultDice.count(dice_msg.qq_id) ? DefaultDice[dice_msg.qq_id] : 100;
				RD rdTurnCnt(strTurnCnt, intDefaultDice);
				const int intRdTurnCntRes = rdTurnCnt.Roll();
				if (intRdTurnCntRes == Value_Err){
					dice_msg.Reply(GlobalMsg["strValueErr"]);
					return;
				}
				if (intRdTurnCntRes == Input_Err){
					dice_msg.Reply(GlobalMsg["strInputErr"]);
					return;
				}
				if (intRdTurnCntRes == ZeroDice_Err){
					dice_msg.Reply(GlobalMsg["strZeroDiceErr"]);
					return;
				}
				if (intRdTurnCntRes == ZeroType_Err){
					dice_msg.Reply(GlobalMsg["strZeroTypeErr"]);
					return;
				}
				if (intRdTurnCntRes == DiceTooBig_Err){
					dice_msg.Reply(GlobalMsg["strDiceTooBigErr"]);
					return;
				}
				if (intRdTurnCntRes == TypeTooBig_Err){
					dice_msg.Reply(GlobalMsg["strTypeTooBigErr"]);
					return;
				}
				if (intRdTurnCntRes == AddDiceVal_Err){
					dice_msg.Reply(GlobalMsg["strAddDiceValErr"]);
					return;
				}
				if (intRdTurnCntRes != 0){
					dice_msg.Reply(GlobalMsg["strUnknownErr"]);
					return;
				}
				if (rdTurnCnt.intTotal > 10){
					dice_msg.Reply(GlobalMsg["strRollTimeExceeded"]);
					return;
				}
				if (rdTurnCnt.intTotal <= 0){
					dice_msg.Reply(GlobalMsg["strRollTimeErr"]);
					return;
				}
				intTurnCnt = rdTurnCnt.intTotal;
				if (strTurnCnt.find('d') != string::npos){
					String strTurnNotice = format(GlobalMsg["strTurnNoticeReplyMsg"],{
						{ "nick", strNickName },
						{ "dice_res", rdTurnCnt.FormShortString()}
					});
					if (!isHidden){
						dice_msg.Reply(strTurnNotice);
					}else{
								strTurnNotice = format(GlobalMsg["strHiddenGroupPrefix"], {
								{ "group_name", getGroupList()[dice_msg.group_id] }
							}) +strTurnNotice;
									AddMsgToQueue(Dice::DiceMsg(strTurnNotice, 0LL, dice_msg.qq_id, Dice::MsgType::Private));
						pair<multimap<long long, long long>::iterator, multimap<long long, long long>::iterator> range;
									range = ObserveGroup.equal_range(dice_msg.group_id);
							for (auto it = range.first; it != range.second; ++it){
							if (it->second != dice_msg.qq_id){
								AddMsgToQueue(Dice::DiceMsg(strTurnNotice, 0LL, it->second, Dice::MsgType::Private));
							}
						}
					}
				}
			}
			if (strMainDice.empty()){
				dice_msg.Reply(GlobalMsg["strEmptyWWDiceErr"]);
				return;
			}
			String strFirstDice = strMainDice.substring(0, strMainDice.find('+') < strMainDice.find('-')? strMainDice.find('+'): strMainDice.find('-'));
			boolean booleanAdda10 = true;
			for (auto i : strFirstDice){
				if (!isdigit(static_cast<unsigned char>(i))){
					booleanAdda10 = false;
					break;
				}
			}
			if (booleanAdda10)
				strMainDice.insert(strFirstDice.length(), "a10");
			const int intDefaultDice = DefaultDice.count(dice_msg.qq_id) ? DefaultDice[dice_msg.qq_id] : 100;
			RD rdMainDice(strMainDice, intDefaultDice);

			const int intFirstTimeRes = rdMainDice.Roll();
			if (intFirstTimeRes == Value_Err){
				dice_msg.Reply(GlobalMsg["strValueErr"]);
				return;
			}
			if (intFirstTimeRes == Input_Err){
				dice_msg.Reply(GlobalMsg["strInputErr"]);
				return;
			}
			if (intFirstTimeRes == ZeroDice_Err){
				dice_msg.Reply(GlobalMsg["strZeroDiceErr"]);
				return;
			} else {
				if (intFirstTimeRes == ZeroType_Err){
					dice_msg.Reply(GlobalMsg["strZeroTypeErr"]);
					return;
				}
				if (intFirstTimeRes == DiceTooBig_Err){
					dice_msg.Reply(GlobalMsg["strDiceTooBigErr"]);
					return;
				}
				if (intFirstTimeRes == TypeTooBig_Err){
					dice_msg.Reply(GlobalMsg["strTypeTooBigErr"]);
					return;
				}
				if (intFirstTimeRes == AddDiceVal_Err){
					dice_msg.Reply(GlobalMsg["strAddDiceValErr"]);
					return;
				}
				if (intFirstTimeRes != 0){
					dice_msg.Reply(GlobalMsg["strUnknownErr"]);
					return;
				}
			}
			if (!booleanDetail && intTurnCnt != 1){
				String strAns = strNickName + "骰出了: " + to_string(intTurnCnt) + "次" + rdMainDice.strDice + ": { ";
				if (!strReason.empty())
					strAns.insert(0, format(GlobalMsg["strRollDiceReasonPrefix"], {
						{"reason", strReason}
					}));
				vector<int> vintExVal;
				while (intTurnCnt--){
					// 此处返回值无用
					// ReSharper disable once CppExpressionWithoutSideEffects
					rdMainDice.Roll();
					strAns += to_string(rdMainDice.intTotal);
					if (intTurnCnt != 0)
						strAns += ",";
					if ((rdMainDice.strDice == "D100" || rdMainDice.strDice == "1D100") && (rdMainDice.intTotal <= 5 ||
						rdMainDice.intTotal >= 96))
						vintExVal.push_back(rdMainDice.intTotal);
				}
				strAns += " }";
				if (!vintExVal.empty()){
					strAns += ",极值: ";
					for (auto it = vintExVal.cbegin(); it != vintExVal.cend(); ++it){
						strAns += to_string(*it);
						if (it != vintExVal.cend() - 1)
							strAns += ",";
					}
				}
				if (!isHidden){
					dice_msg.Reply(strAns);
				} else {
							strAns = format(GlobalMsg["strHiddenGroupPrefix"], {
							{ "group_name", getGroupList()[dice_msg.group_id]}
						});
						AddMsgToQueue(Dice::DiceMsg(strAns, 0LL, dice_msg.qq_id, Dice::MsgType::Private));
					pair<multimap<long long, long long>::iterator, multimap<long long, long long>::iterator> range;
									range = ObserveGroup.equal_range(dice_msg.group_id);
						for (auto it = range.first; it != range.second; ++it){
						if (it->second != dice_msg.qq_id){
							AddMsgToQueue(Dice::DiceMsg(strAns, 0LL, it->second, Dice::MsgType::Private));
						}
					}
				}
			} else {
				while (intTurnCnt--){
					// 此处返回值无用
					// ReSharper disable once CppExpressionWithoutSideEffects
					rdMainDice.Roll();
					String strAns = format(GlobalMsg["strRollDiceReplyMsg"], {
						{ "nick", strNickName },
						{ "dice_res", booleanDetail? rdMainDice.FormCompleteString(): rdMainDice.FormShortString() }
					});
					if (!strReason.empty())
						strAns.insert(0, format(GlobalMsg["strRollDiceReasonPrefix"], {
							{ "reason", strReason }
						}));
					if (!isHidden){
						dice_msg.Reply(strAns);
					}else{
							strAns = format(GlobalMsg["strHiddenGroupPrefix"], {
								{ "group_name", getGroupList()[dice_msg.group_id] }
							}) + strAns;
							AddMsgToQueue(Dice::DiceMsg(strAns, 0LL, dice_msg.qq_id, Dice::MsgType::Private));
						pair<multimap<long long, long long>::iterator, multimap<long long, long long>::iterator> range;
										range = ObserveGroup.equal_range(dice_msg.group_id);
							for (auto it = range.first; it != range.second; ++it){
							if (it->second != dice_msg.qq_id){
								AddMsgToQueue(Dice::DiceMsg(strAns, 0LL, it->second, Dice::MsgType::Private));
							}
						}
					}
				}
			}
			if (isHidden){
				dice_msg.Reply(format(GlobalMsg["strHiddenDiceReplyMsg"], {
					{ "nick", strNickName }
				}));
			}
		}else if (strLowerMessage.substring(intMsgCnt, 2) == "ob"){
			if (dice_msg.msg_type == Dice::MsgType::Private){
				dice_msg.Reply(GlobalMsg["strCommandNotAvailableErr"]);
				return;
			}
			intMsgCnt += 2;
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])))
				intMsgCnt++;
			const String Command = strLowerMessage.substring(intMsgCnt, dice_msg.msg.find(' ', intMsgCnt) - intMsgCnt);
			if (Command == "on"){
				if (dice_msg.msg_type == Dice::MsgType::Group){
					if (getGroupMemberInfo(dice_msg.group_id, dice_msg.qq_id).permissions >= 2)
					{
						if (DisabledOBGroup.count(dice_msg.group_id))
						{
							DisabledOBGroup.erase(dice_msg.group_id);
							dice_msg.Reply(GlobalMsg["strObCommandSuccessfullyEnabledNotice"]);
						}
						else
						{
							dice_msg.Reply(GlobalMsg["strObCommandAlreadyEnabledErr"]);
						}
					}
					else
					{
						dice_msg.Reply(GlobalMsg["strPermissionDeniedErr"]);
					}
				} else if (dice_msg.msg_type == Dice::MsgType::Discuss){
					if (DisabledOBDiscuss.count(dice_msg.group_id))
					{
						DisabledOBDiscuss.erase(dice_msg.group_id);
						dice_msg.Reply(GlobalMsg["strObCommandSuccessfullyEnabledNotice"]);
					}
					else
					{
						dice_msg.Reply(GlobalMsg["strObCommandAlreadyEnabledErr"]);
					}
				}

				return;
			}
			if (Command == "off"){
				if (dice_msg.msg_type == Dice::MsgType::Group){
					if (getGroupMemberInfo(dice_msg.group_id, dice_msg.qq_id).permissions >= 2)
					{
						if (!DisabledOBGroup.count(dice_msg.group_id))
						{
							DisabledOBGroup.insert(dice_msg.group_id);
							ObserveGroup.erase(dice_msg.group_id);
							dice_msg.Reply(GlobalMsg["strObCommandSuccessfullyDisabledNotice"]);
						}
						else
						{
							dice_msg.Reply(GlobalMsg["strObCommandAlreadyDisabledErr"]);
						}
					}
					else
					{
						dice_msg.Reply(GlobalMsg["strPermissionDeniedErr"]);
					}
				} else if (dice_msg.msg_type == Dice::MsgType::Discuss){
					if (!DisabledOBDiscuss.count(dice_msg.group_id))
					{
						DisabledOBDiscuss.insert(dice_msg.group_id);
						ObserveDiscuss.erase(dice_msg.group_id);
						dice_msg.Reply(GlobalMsg["strObCommandSuccessfullyDisabledNotice"]);
					}
					else
					{
						dice_msg.Reply(GlobalMsg["strObCommandAlreadyDisabledErr"]);
					}
				}
				return;
			}
			if ( (dice_msg.msg_type == Dice::MsgType::Group && DisabledOBGroup.count(dice_msg.group_id)) || (dice_msg.msg_type == Dice::MsgType::Discuss && DisabledOBDiscuss.count(dice_msg.group_id))){
				dice_msg.Reply(GlobalMsg["strObCommandDisabledErr"]);
				return;
			}
			if (Command == "list"){
				pair<multimap<long long, long long>::iterator, multimap<long long, long long>::iterator> range;
				if (dice_msg.msg_type == Dice::MsgType::Group){
					range = ObserveGroup.equal_range(dice_msg.group_id);
				} else if (dice_msg.msg_type == Dice::MsgType::Discuss){
					range = ObserveDiscuss.equal_range(dice_msg.group_id);
				}
				if (range.first == range.second){
					dice_msg.Reply(GlobalMsg["strObListEmptyNotice"]);
				} else {
					String strObList;
					for (auto it = range.first; it != range.second; ++it)
					{
						strObList += "\n" + getName(it->second, dice_msg.group_id) + "(" + to_string(it->second) + ")";
					}
					dice_msg.Reply(format(GlobalMsg["strObListReplyMsg"], {
						{ "ob_list", strObList }
					}));
				}

			} else if (Command == "clr"){
				if (dice_msg.msg_type == Dice::MsgType::Group){
					if (getGroupMemberInfo(dice_msg.group_id, dice_msg.qq_id).permissions >= 2)
					{
						ObserveGroup.erase(dice_msg.group_id);
						dice_msg.Reply(GlobalMsg["strObListClearedNotice"]);
					}
					else
					{
						dice_msg.Reply(GlobalMsg["strPermissionDeniedErr"]);
					}
				} else if (dice_msg.msg_type == Dice::MsgType::Discuss){
					ObserveDiscuss.erase(dice_msg.group_id);
					dice_msg.Reply(GlobalMsg["strObListClearedNotice"]);
				}

			} else if (Command == "exit"){
				pair<multimap<long long, long long>::iterator, multimap<long long, long long>::iterator> range;
				if (dice_msg.msg_type == Dice::MsgType::Group){
					range = ObserveGroup.equal_range(dice_msg.group_id);
				} else if (dice_msg.msg_type == Dice::MsgType::Discuss){
					range = ObserveDiscuss.equal_range(dice_msg.group_id);
				}
				for (auto it = range.first; it != range.second; ++it){
					if (it->second == dice_msg.qq_id)
					{
						if (dice_msg.msg_type == Dice::MsgType::Group)
						{
							ObserveGroup.erase(it);
						}
						else if (dice_msg.msg_type == Dice::MsgType::Discuss)
						{
							ObserveDiscuss.erase(it);
						}
						const String strReply = strNickName + "成功退出旁观模式!";
						dice_msg.Reply(strReply);
						return;
					}
				}
				const String strReply = strNickName + "没有加入旁观模式!";
				dice_msg.Reply(strReply);
			} else {
				pair<multimap<long long, long long>::iterator, multimap<long long, long long>::iterator> range;
				if (dice_msg.msg_type == Dice::MsgType::Group){
					range = ObserveGroup.equal_range(dice_msg.group_id);
				} else if (dice_msg.msg_type == Dice::MsgType::Discuss){
					range = ObserveDiscuss.equal_range(dice_msg.group_id);
				}
				for (auto it = range.first; it != range.second; ++it){
					if (it->second == dice_msg.qq_id)
					{
						const String strReply = strNickName + "已经处于旁观模式!";
						dice_msg.Reply(strReply);
						return;
					}
				}
				if (dice_msg.msg_type == Dice::MsgType::Group){
					ObserveGroup.insert(make_pair(dice_msg.group_id, dice_msg.qq_id));
				} else if (dice_msg.msg_type == Dice::MsgType::Discuss){
					ObserveDiscuss.insert(make_pair(dice_msg.group_id, dice_msg.qq_id));
				}
				
				const String strReply = strNickName + "成功加入旁观模式!";
				dice_msg.Reply(strReply);
			}
		}else if (strLowerMessage.substring(intMsgCnt, 2) == "ti"){
			String strAns = strNickName + "的疯狂发作-临时症状:\n";
			TempInsane(strAns);
			dice_msg.Reply(strAns);
		}else if (strLowerMessage.substring(intMsgCnt, 2) == "li"){
			String strAns = strNickName + "的疯狂发作-总结症状:\n";
			LongInsane(strAns);
			dice_msg.Reply(strAns);
		}else if (strLowerMessage.substring(intMsgCnt, 2) == "sc"){
			intMsgCnt += 2;
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])))
				intMsgCnt++;
			String SanCost = strLowerMessage.substring(intMsgCnt, dice_msg.msg.find(' ', intMsgCnt) - intMsgCnt);
			intMsgCnt += SanCost.length();
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])))
				intMsgCnt++;
			String San;
			while (isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt]))){
				San += strLowerMessage[intMsgCnt];
				intMsgCnt++;
			}
			if (SanCost.empty() || SanCost.find('/') == string::npos){
				dice_msg.Reply(GlobalMsg["strSCInvalid"]);
				return;
			}
			if (San.empty() && !(CharacterProp.count(SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)) && CharacterProp[
				SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)].count("理智"))){
				dice_msg.Reply(GlobalMsg["strSanInvalid"]);
				return;
			}
				for (const auto& character : SanCost.substring(0, SanCost.find('/'))){
					if (!isdigit(static_cast<unsigned char>(character)) && character != 'D' && character != 'd' && character != '+' && character != '-')
					{
						dice_msg.Reply(GlobalMsg["strSCInvalid"]);
						return;
					}
				}
				for (const auto& character : SanCost.substring(SanCost.find('/') + 1)){
					if (!isdigit(static_cast<unsigned char>(character)) && character != 'D' && character != 'd' && character != '+' && character != '-')
					{
						dice_msg.Reply(GlobalMsg["strSCInvalid"]);
						return;
					}
				}
				RD rdSuc(SanCost.substring(0, SanCost.find('/')));
				RD rdFail(SanCost.substring(SanCost.find('/') + 1));
				if (rdSuc.Roll() != 0 || rdFail.Roll() != 0){
					dice_msg.Reply(GlobalMsg["strSCInvalid"]);
					return;
				}
				if (San.length() >= 3){
					dice_msg.Reply(GlobalMsg["strSanInvalid"]);
					return;
				}
				const int intSan = San.empty() ? CharacterProp[SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)]["理智"] : stoi(San);
				if (intSan == 0){
					dice_msg.Reply(GlobalMsg["strSanInvalid"]);
					return;
				}
				String strAns = strNickName + "的Sancheck:\n1D100=";
				const int intTmpRollRes = RandomGenerator::Randint(1, 100);
				strAns += to_string(intTmpRollRes);

				if (intTmpRollRes <= intSan){
					strAns += " " + GlobalMsg["strSuccess"] + "\n你的San值减少" + SanCost.substring(0, SanCost.find('/'));
					if (SanCost.substring(0, SanCost.find('/')).find('d') != string::npos)
						strAns += "=" + to_string(rdSuc.intTotal);
					strAns += +"点,当前剩余" + to_string(max(0, intSan - rdSuc.intTotal)) + "点";
					if (San.empty())
					{
						CharacterProp[SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)]["理智"] = max(0, intSan - rdSuc.intTotal);
					}
				} else if (intTmpRollRes == 100 || (intSan < 50 && intTmpRollRes > 95)){
					strAns += " " + GlobalMsg["strFumble"] + "\n你的San值减少" + SanCost.substring(SanCost.find('/') + 1);
					// ReSharper disable once CppExpressionWithoutSideEffects
					rdFail.Max();
					if (SanCost.substring(SanCost.find('/') + 1).find('d') != string::npos)
						strAns += "最大值=" + to_string(rdFail.intTotal);
					strAns += +"点,当前剩余" + to_string(max(0, intSan - rdFail.intTotal)) + "点";
					if (San.empty())
					{
						CharacterProp[SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)]["理智"] = max(0, intSan - rdFail.intTotal);
					}
				} else {
					strAns += " " + GlobalMsg["strFailure"] + "\n你的San值减少" + SanCost.substring(SanCost.find('/') + 1);
					if (SanCost.substring(SanCost.find('/') + 1).find('d') != string::npos)
						strAns += "=" + to_string(rdFail.intTotal);
					strAns += +"点,当前剩余" + to_string(max(0, intSan - rdFail.intTotal)) + "点";
					if (San.empty())
					{
						CharacterProp[SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)]["理智"] = max(0, intSan - rdFail.intTotal);
					}
				}
				dice_msg.Reply(strAns);
		}else if (strLowerMessage.substring(intMsgCnt, 2) == "en"){
			intMsgCnt += 2;
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])))
				intMsgCnt++;
			String strSkillName;
			while (intMsgCnt != dice_msg.msg.length() && !isdigit(static_cast<unsigned char>(dice_msg.msg[intMsgCnt])) && !isspace(static_cast<unsigned char>(dice_msg.msg[intMsgCnt]))
				){
				strSkillName += strLowerMessage[intMsgCnt];
				intMsgCnt++;
			}
			if (SkillNameReplace.count(strSkillName))strSkillName = SkillNameReplace[strSkillName];
			while (isspace(static_cast<unsigned char>(dice_msg.msg[intMsgCnt])))
				intMsgCnt++;
			String strCurrentValue;
			while (isdigit(static_cast<unsigned char>(dice_msg.msg[intMsgCnt]))){
				strCurrentValue += dice_msg.msg[intMsgCnt];
				intMsgCnt++;
			}
			int intCurrentVal;
			if (strCurrentValue.empty()){
				if (CharacterProp.count(SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)) && CharacterProp[SourceType(
					dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)].count(strSkillName)){
					intCurrentVal = CharacterProp[SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)][strSkillName];
				} else if (SkillDefaultVal.count(strSkillName)){
					intCurrentVal = SkillDefaultVal[strSkillName];
				} else {
					dice_msg.Reply(GlobalMsg["strEnValInvalid"]);
					return;
				}
			} else {
				if (strCurrentValue.length() > 3){
					dice_msg.Reply(GlobalMsg["strEnValInvalid"]);

					return;
				}
				intCurrentVal = stoi(strCurrentValue);
			}

			String strAns = strNickName + "的" + strSkillName + "增强或成长检定:\n1D100=";
			const int intTmpRollRes = RandomGenerator::Randint(1, 100);
			strAns += to_string(intTmpRollRes) + "/" + to_string(intCurrentVal);

			if (intTmpRollRes <= intCurrentVal && intTmpRollRes <= 95){
				strAns += " " + GlobalMsg["strFailure"] + "\n你的" + (strSkillName.empty() ? "属性或技能值" : strSkillName) + "没有变化!";
			} else {
				strAns += " " + GlobalMsg["strSuccess"] + "\n你的" + (strSkillName.empty() ? "属性或技能值" : strSkillName) + "增加1D10=";
				const int intTmpRollD10 = RandomGenerator::Randint(1, 10);
				strAns += to_string(intTmpRollD10) + "点,当前为" + to_string(intCurrentVal + intTmpRollD10) + "点";
				if (strCurrentValue.empty()){
					CharacterProp[SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)][strSkillName] = intCurrentVal +
						intTmpRollD10;
				}
			}
			dice_msg.Reply(strAns);
		}else if (strLowerMessage.substring(intMsgCnt, 4) == "jrrp"){
			intMsgCnt += 4;
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])))
				intMsgCnt++;
			const String Command = strLowerMessage.substring(intMsgCnt, dice_msg.msg.find(' ', intMsgCnt) - intMsgCnt);
			if (Command == "on"){
				if (dice_msg.msg_type == Dice::MsgType::Group){
					if (getGroupMemberInfo(dice_msg.group_id, dice_msg.qq_id).permissions >= 2)
					{
						if (DisabledJRRPGroup.count(dice_msg.group_id))
						{
							DisabledJRRPGroup.erase(dice_msg.group_id);
							dice_msg.Reply(GlobalMsg["strJrrpCommandSuccessfullyEnabledNotice"]);
						}
						else
						{
							dice_msg.Reply(GlobalMsg["strJrrpCommandAlreadyEnabledErr"]);
						}
					}
					else
					{
						dice_msg.Reply(GlobalMsg["strPermissionDeniedErr"]);
					}
				} else if (dice_msg.msg_type == Dice::MsgType::Discuss){
					if (DisabledJRRPDiscuss.count(dice_msg.group_id))
					{
						DisabledJRRPDiscuss.erase(dice_msg.group_id);
						dice_msg.Reply(GlobalMsg["strJrrpCommandSuccessfullyEnabledNotice"]);
					}
					else
					{
						dice_msg.Reply(GlobalMsg["strJrrpCommandAlreadyEnabledErr"]);
					}
				}
				return;
			}
			if (Command == "off"){
				if (dice_msg.msg_type == Dice::MsgType::Group){
					if (getGroupMemberInfo(dice_msg.group_id, dice_msg.qq_id).permissions >= 2)
					{
						if (!DisabledJRRPGroup.count(dice_msg.group_id))
						{
							DisabledJRRPGroup.insert(dice_msg.group_id);
							dice_msg.Reply(GlobalMsg["strJrrpCommandSuccessfullyDisabledNotice"]);
						}
						else
						{
							dice_msg.Reply(GlobalMsg["strJrrpCommandAlreadyDisabledErr"]);
						}
					}
					else
					{
						dice_msg.Reply(GlobalMsg["strPermissionDeniedErr"]);
					}
				} else if (dice_msg.msg_type == Dice::MsgType::Discuss){
					if (!DisabledJRRPDiscuss.count(dice_msg.group_id))
					{
						DisabledJRRPDiscuss.insert(dice_msg.group_id);
						dice_msg.Reply(GlobalMsg["strJrrpCommandSuccessfullyDisabledNotice"]);
					}
					else
					{
						dice_msg.Reply(GlobalMsg["strJrrpCommandAlreadyDisabledErr"]);
					}
				}
				return;
			}
			if ((dice_msg.msg_type==Dice::MsgType::Group && DisabledJRRPGroup.count(dice_msg.group_id)) || (dice_msg.msg_type == Dice::MsgType::Discuss && DisabledJRRPDiscuss.count(dice_msg.group_id)) ){
				dice_msg.Reply(GlobalMsg["strJrrpCommandDisabledErr"]);
				return;
			}
			String des;
			String data = "QQ=" + to_string(CQ::getLoginQQ()) + "&v=20190114" + "&QueryQQ=" + to_string(dice_msg.qq_id);
			char* frmdata = new char[data.length() + 1];
			strcpy_s(frmdata, data.length() + 1, data.c_str());
			boolean res = Network::POST("api.kokona.tech", "/jrrp", 5555, frmdata, des);
			delete[] frmdata;
			if (res){
				dice_msg.Reply(format(GlobalMsg["strJrrp"], 
					{ 
						{ "nick", strNickName }, 
						{ "jrrp_value", des } 
					}
				));
			} else {
				dice_msg.Reply(format(GlobalMsg["strJrrpErr"], 
					{
						{ "error_msg" , des }
					}
				));
			}
		}else if (strLowerMessage.substring(intMsgCnt, 4) == "name"){
			intMsgCnt += 4;
			while (isspace(static_cast<unsigned char>(dice_msg.msg[intMsgCnt])))
				intMsgCnt++;

			String type;
			while (isalpha(static_cast<unsigned char>(strLowerMessage[intMsgCnt]))){
				type += strLowerMessage[intMsgCnt];
				intMsgCnt++;
			}

			auto nameType = NameGenerator::Type::UNKNOWN;
			if (type == "cn")
				nameType = NameGenerator::Type::CN;
			else if (type == "en")
				nameType = NameGenerator::Type::EN;
			else if (type == "jp")
				nameType = NameGenerator::Type::JP;

			while (isspace(static_cast<unsigned char>(dice_msg.msg[intMsgCnt])))
				intMsgCnt++;

			String strNum;
			while (isdigit(static_cast<unsigned char>(dice_msg.msg[intMsgCnt]))){
				strNum += dice_msg.msg[intMsgCnt];
				intMsgCnt++;
			}
			if (strNum.size() > 2){
				dice_msg.Reply(GlobalMsg["strNameNumTooBig"]);
				return;
			}
			int intNum = stoi(strNum.empty() ? "1" : strNum);
			if (intNum > 10){
				dice_msg.Reply(GlobalMsg["strNameNumTooBig"]);
				return;
			}
			if (intNum == 0){
				dice_msg.Reply(GlobalMsg["strNameNumCannotBeZero"]);
				return;
			}
			vector<string> TempNameStorage;
			while (TempNameStorage.size() != intNum){
				String name = NameGenerator::getRandomName(nameType);
				if (find(TempNameStorage.begin(), TempNameStorage.end(), name) == TempNameStorage.end()){
					TempNameStorage.push_back(name);
				}
			}
			String strReply = strNickName + "的随机名称:\n";
			for (auto i = 0; i != TempNameStorage.size(); i++){
				strReply.append(TempNameStorage[i]);
				if (i != TempNameStorage.size() - 1)strReply.append(", ");
			}
			dice_msg.Reply(strReply);
		}else if (strLowerMessage.substring(intMsgCnt, 3) == "nnn"){
			intMsgCnt += 3;
			while (isspace(static_cast<unsigned char>(dice_msg.msg[intMsgCnt])))
				intMsgCnt++;
			String type = strLowerMessage.substring(intMsgCnt, 2);
			String name;
			if (type == "cn")
				name = NameGenerator::getChineseName();
			else if (type == "en")
				name = NameGenerator::getEnglishName();
			else if (type == "jp")
				name = NameGenerator::getJapaneseName();
			else
				name = NameGenerator::getRandomName();
			if (dice_msg.msg_type == Dice::MsgType::Private){
				Name->set(0LL, dice_msg.qq_id, name);
			}else{
				Name->set(dice_msg.group_id, dice_msg.qq_id, name);
			}
			dice_msg.Reply(format(GlobalMsg["strNickChangeReplyMsg"], {
				{"nick", strNickName},
				{"is_global", dice_msg.msg_type == Dice::MsgType::Private ? "全局" : ""},
				{"new_nick", name}
			}));
		}else if (strLowerMessage.substring(intMsgCnt, 2) == "nn"){
			if (dice_msg.msg_type == Dice::MsgType::Private){
				dice_msg.Reply(GlobalMsg["strCommandNotAvailableErr"]);
				return;
			}
			intMsgCnt += 2;
			while (isspace(static_cast<unsigned char>(dice_msg.msg[intMsgCnt])))
				intMsgCnt++;
			String name = dice_msg.msg.substring(intMsgCnt);
			if (name.length() > 50){
				dice_msg.Reply(GlobalMsg["strNameTooLongErr"]);
				return;
			}
			if (!name.empty()){
				Name->set(dice_msg.group_id, dice_msg.qq_id, name);
				dice_msg.Reply(format(GlobalMsg["strNickChangeReplyMsg"], {
					{"nick", strNickName},
					{"is_global", ""},
					{"new_nick", strip(name)}
				}));
			} else {
				if (Name->del(dice_msg.group_id, dice_msg.qq_id)){
					dice_msg.Reply(format(GlobalMsg["strNickDeleteReplyMsg"], {
						{"nick", strNickName},
						{"is_global", ""}
					}));
				} else {
					const String strReply = strNickName + GlobalMsg["strNameDelErr"];
					dice_msg.Reply(strReply);
				}
			}
		}else if (strLowerMessage[intMsgCnt] == 'n'){
			intMsgCnt += 1;
			while (isspace(static_cast<unsigned char>(dice_msg.msg[intMsgCnt])))
				intMsgCnt++;
			String name = dice_msg.msg.substring(intMsgCnt);
			if (name.length() > 50){
				dice_msg.Reply(GlobalMsg["strNameTooLongErr"]);
				return;
			}
			if (!name.empty()){
				Name->set(0LL, dice_msg.qq_id, name);
				dice_msg.Reply(format(GlobalMsg["strNickChangeReplyMsg"], {
					{"nick", strNickName},
					{"is_global", "全局"},
					{"new_nick", strip(name)}
				}));
			} else {
				if (Name->del(0LL, dice_msg.qq_id)){
					dice_msg.Reply(format(GlobalMsg["strNickDeleteReplyMsg"], {
						{"nick", strNickName},
						{"is_global", "全局"}
					}));
				} else {
					const String strReply = strNickName + GlobalMsg["strNameDelErr"];
					dice_msg.Reply(strReply);
				}
			}
		}else if (strLowerMessage.substring(intMsgCnt, 5) == "rules"){
			intMsgCnt += 5;
			while (isspace(static_cast<unsigned char>(dice_msg.msg[intMsgCnt])))
				intMsgCnt++;
			String strSearch = dice_msg.msg.substring(intMsgCnt);
			for (auto& n : strSearch)
				n = toupper(static_cast<unsigned char>(n));
			String strReturn;
			if (GetRule::analyze(strSearch, strReturn)){
				dice_msg.Reply(strReturn);
			} else {
				dice_msg.Reply(GlobalMsg["strRuleErr"] + strReturn);
			}
		}else if (strLowerMessage.substring(intMsgCnt, 3) == "set"){
			intMsgCnt += 3;
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])))
				intMsgCnt++;
			String strDefaultDice = strLowerMessage.substring(intMsgCnt, strLowerMessage.find(' ', intMsgCnt) - intMsgCnt);
			while (strDefaultDice[0] == '0')
				strDefaultDice.erase(strDefaultDice.begin());
			if (strDefaultDice.empty())
				strDefaultDice = "100";
			for (auto charNumElement : strDefaultDice)
				if (!isdigit(static_cast<unsigned char>(charNumElement))){
					dice_msg.Reply(GlobalMsg["strSetInvalid"]);
					return;
				}
			if (strDefaultDice.length() > 5){
				dice_msg.Reply(GlobalMsg["strSetTooBig"]);
				return;
			}
			const int intDefaultDice = stoi(strDefaultDice);
			if (intDefaultDice == 100)
				DefaultDice.erase(dice_msg.qq_id);
			else
				DefaultDice[dice_msg.qq_id] = intDefaultDice;
			const String strSetSuccessReply = "已将" + strNickName + "的默认骰类型更改为D" + strDefaultDice;
			dice_msg.Reply(strSetSuccessReply);
		}else if (strLowerMessage.substring(intMsgCnt, 5) == "coc6d"){
			String strReply = strNickName;
			COC6D(strReply);
			dice_msg.Reply(strReply);
		}else if (strLowerMessage.substring(intMsgCnt, 4) == "coc6"){
			intMsgCnt += 4;
			if (strLowerMessage[intMsgCnt] == 's')
				intMsgCnt++;
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])))
				intMsgCnt++;
			String strNum;
			while (isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt]))){
				strNum += strLowerMessage[intMsgCnt];
				intMsgCnt++;
			}
			if (strNum.length() > 2){
				dice_msg.Reply(GlobalMsg["strCharacterTooBig"]);
				return;
			}
			const int intNum = stoi(strNum.empty() ? "1" : strNum);
			if (intNum > 10){
				dice_msg.Reply(GlobalMsg["strCharacterTooBig"]);
				return;
			}
			if (intNum == 0){
				dice_msg.Reply(GlobalMsg["strCharacterCannotBeZero"]);
				return;
			}
			String strReply = strNickName;
			COC6(strReply, intNum);
			dice_msg.Reply(strReply);
		}else if (strLowerMessage.substring(intMsgCnt, 3) == "dnd"){
			intMsgCnt += 3;
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])))
				intMsgCnt++;
			String strNum;
			while (isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt]))){
				strNum += strLowerMessage[intMsgCnt];
				intMsgCnt++;
			}
			if (strNum.length() > 2){
				dice_msg.Reply(GlobalMsg["strCharacterTooBig"]);
				return;
			}
			const int intNum = stoi(strNum.empty() ? "1" : strNum);
			if (intNum > 10){
				dice_msg.Reply(GlobalMsg["strCharacterTooBig"]);
				return;
			}
			if (intNum == 0){
				dice_msg.Reply(GlobalMsg["strCharacterCannotBeZero"]);
				return;
			}
			String strReply = strNickName;
			DND(strReply, intNum);
			dice_msg.Reply(strReply);
		}else if (strLowerMessage.substring(intMsgCnt, 5) == "coc7d" || strLowerMessage.substring(intMsgCnt, 4) == "cocd"){
			String strReply = strNickName;
			COC7D(strReply);
			dice_msg.Reply(strReply);
		}else if (strLowerMessage.substring(intMsgCnt, 3) == "coc"){
			intMsgCnt += 3;
			if (strLowerMessage[intMsgCnt] == '7')
				intMsgCnt++;
			if (strLowerMessage[intMsgCnt] == 's')
				intMsgCnt++;
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])))
				intMsgCnt++;
			String strNum;
			while (isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt]))){
				strNum += strLowerMessage[intMsgCnt];
				intMsgCnt++;
			}
			if (strNum.length() > 2){
				dice_msg.Reply(GlobalMsg["strCharacterTooBig"]);
				return;
			}
			const int intNum = stoi(strNum.empty() ? "1" : strNum);
			if (intNum > 10){
				dice_msg.Reply(GlobalMsg["strCharacterTooBig"]);
				return;
			}
			if (intNum == 0){
				dice_msg.Reply(GlobalMsg["strCharacterCannotBeZero"]);
				return;
			}
			String strReply = strNickName;
			COC7(strReply, intNum);
			dice_msg.Reply(strReply);
		}else if (strLowerMessage.substring(intMsgCnt, 2) == "ra"){
			intMsgCnt += 2;
			String strSkillName;
			while (strLowerMessage.charAt(intMsgCnt++)==' ');
while (intMsgCnt != strLowerMessage.length() && !isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) && !
				isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) && strLowerMessage[intMsgCnt] != '=' && strLowerMessage[intMsgCnt] !=
				':'){
				strSkillName += strLowerMessage[intMsgCnt];
				intMsgCnt++;
			}
			if (SkillNameReplace.count(strSkillName))strSkillName = SkillNameReplace[strSkillName];
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) || strLowerMessage[intMsgCnt] == '=' || strLowerMessage[intMsgCnt] ==
				':')intMsgCnt++;
			String strSkillVal;
			while (isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt]))){
				strSkillVal += strLowerMessage[intMsgCnt];
				intMsgCnt++;
			}
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt]))){
				intMsgCnt++;
			}
			String strReason = dice_msg.msg.substring(intMsgCnt);
			int intSkillVal;
			if (strSkillVal.empty()){
				if (CharacterProp.count(SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)) && CharacterProp[SourceType(
					dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)].count(strSkillName)){
					intSkillVal = CharacterProp[SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)][strSkillName];
				} else if (SkillDefaultVal.count(strSkillName)){
					intSkillVal = SkillDefaultVal[strSkillName];
				} else {
					dice_msg.Reply(GlobalMsg["strUnknownPropErr"]);
					return;
				}
			} else if (strSkillVal.length() > 3){
				dice_msg.Reply(GlobalMsg["strPropErr"]);
				return;
			} else {
				intSkillVal = stoi(strSkillVal);
			}
			const int intD100Res = RandomGenerator::Randint(1, 100);
			String strReply = strNickName + "进行" + strSkillName + "检定: D100=" + to_string(intD100Res) + "/" +
				to_string(intSkillVal) + " ";
			if (intD100Res <= 5 && intD100Res <= intSkillVal)strReply += GlobalMsg["strCriticalSuccess"];
			else if (intD100Res > 95)strReply += GlobalMsg["strFumble"];
			else if (intD100Res <= intSkillVal / 5)strReply += GlobalMsg["strExtremeSuccess"];
			else if (intD100Res <= intSkillVal / 2)strReply += GlobalMsg["strHardSuccess"];
			else if (intD100Res <= intSkillVal)strReply += GlobalMsg["strSuccess"];
			else strReply += GlobalMsg["strFailure"];
			
			if (!strReason.empty()){
				strReply = "由于" + strReason + " " + strReply;
			}
			dice_msg.Reply(strReply);
		}else if (strLowerMessage.substring(intMsgCnt, 2) == "rc"){
			intMsgCnt += 2;
			String strSkillName;
			while (strLowerMessage.charAt(intMsgCnt++)==' ');
while (intMsgCnt != strLowerMessage.length() && !isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) && !
				isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) && strLowerMessage[intMsgCnt] != '=' && strLowerMessage[intMsgCnt] !=
				':'){
				strSkillName += strLowerMessage[intMsgCnt];
				intMsgCnt++;
			}
			if (SkillNameReplace.count(strSkillName))strSkillName = SkillNameReplace[strSkillName];
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt])) || strLowerMessage[intMsgCnt] == '=' || strLowerMessage[intMsgCnt] ==
				':')intMsgCnt++;
			String strSkillVal;
			while (isdigit(static_cast<unsigned char>(strLowerMessage[intMsgCnt]))){
				strSkillVal += strLowerMessage[intMsgCnt];
				intMsgCnt++;
			}
			while (isspace(static_cast<unsigned char>(strLowerMessage[intMsgCnt]))){
				intMsgCnt++;
			}
			String strReason = dice_msg.msg.substring(intMsgCnt);
			int intSkillVal;
			if (strSkillVal.empty()){
				if (CharacterProp.count(SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)) && CharacterProp[SourceType(
					dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)].count(strSkillName)){
					intSkillVal = CharacterProp[SourceType(dice_msg.qq_id, dice_msg.msg_type, dice_msg.group_id)][strSkillName];
				} else if (SkillDefaultVal.count(strSkillName)){
					intSkillVal = SkillDefaultVal[strSkillName];
				} else {
					dice_msg.Reply(GlobalMsg["strUnknownPropErr"]);
					return;
				}
			} else if (strSkillVal.length() > 3){
				dice_msg.Reply(GlobalMsg["strPropErr"]);
				return;
			} else {
				intSkillVal = stoi(strSkillVal);
			}
			const int intD100Res = RandomGenerator::Randint(1, 100);
			String strReply = strNickName + "进行" + strSkillName + "检定: D100=" + to_string(intD100Res) + "/" +
				to_string(intSkillVal) + " ";
			if (intSkillVal != 0 && intD100Res == 1)strReply += GlobalMsg["strCriticalSuccess"];
			else if (intD100Res == 100 || (intSkillVal < 50 && intD100Res > 95)) strReply += GlobalMsg["strFumble"];
			else if (intD100Res <= intSkillVal / 5)strReply += GlobalMsg["strExtremeSuccess"];
			else if (intD100Res <= intSkillVal / 2)strReply += GlobalMsg["strHardSuccess"];
			else if (intD100Res <= intSkillVal)strReply += GlobalMsg["strSuccess"];
			else strReply += GlobalMsg["strFailure"];
			
			if (!strReason.empty()){
				strReply = "由于" + strReason + " " + strReply;
			}
			dice_msg.Reply(strReply);
		}else if (strLowerMessage[intMsgCnt] == 'r'){
			intMsgCnt += 1;
			boolean booleanDetail = true, isHidden = false;
			if (dice_msg.msg[intMsgCnt] == 's'){
				booleanDetail = false;
				intMsgCnt++;
			}
			if (strLowerMessage[intMsgCnt] == 'h'){
				isHidden = true;
				intMsgCnt++;
			}
			while (isspace(static_cast<unsigned char>(dice_msg.msg[intMsgCnt])))
				intMsgCnt++;
			String strMainDice;
			String strReason;
			boolean tmpContainD = false;
			int intTmpMsgCnt;
			for (intTmpMsgCnt = intMsgCnt; intTmpMsgCnt != dice_msg.msg.length() && dice_msg.msg[intTmpMsgCnt] != ' ';
				intTmpMsgCnt++){
				if (strLowerMessage[intTmpMsgCnt] == 'd' || strLowerMessage[intTmpMsgCnt] == 'p' || strLowerMessage[
					intTmpMsgCnt] == 'b' || strLowerMessage[intTmpMsgCnt] == '#' || strLowerMessage[intTmpMsgCnt] == 'f'
						|| strLowerMessage[intTmpMsgCnt] == 'a' || strLowerMessage[intTmpMsgCnt] == 'x' || strLowerMessage[intTmpMsgCnt] == '*')
					tmpContainD = true;
					if (!isdigit(static_cast<unsigned char>(strLowerMessage[intTmpMsgCnt])) && strLowerMessage[intTmpMsgCnt] != 'd' && strLowerMessage[
						intTmpMsgCnt] != 'k' && strLowerMessage[intTmpMsgCnt] != 'p' && strLowerMessage[intTmpMsgCnt] != 'b'
							&&
							strLowerMessage[intTmpMsgCnt] != 'f' && strLowerMessage[intTmpMsgCnt] != '+' && strLowerMessage[
								intTmpMsgCnt
							] != '-' && strLowerMessage[intTmpMsgCnt] != '#' && strLowerMessage[intTmpMsgCnt] != 'a' && strLowerMessage[intTmpMsgCnt] != 'x'&& strLowerMessage[intTmpMsgCnt] != '*')
					{
						break;
					}
			}
			if (tmpContainD){
				strMainDice = strLowerMessage.substring(intMsgCnt, intTmpMsgCnt - intMsgCnt);
				while (isspace(static_cast<unsigned char>(dice_msg.msg[intTmpMsgCnt])))
					intTmpMsgCnt++;
				strReason = dice_msg.msg.substring(intTmpMsgCnt);
			} else
				strReason = dice_msg.msg.substring(intMsgCnt);

			int intTurnCnt = 1;
			if (strMainDice.find('#') != string::npos){
				String strTurnCnt = strMainDice.substring(0, strMainDice.find('#'));
				if (strTurnCnt.empty())
					strTurnCnt = "1";
				strMainDice = strMainDice.substring(strMainDice.find('#') + 1);
				const int intDefaultDice = DefaultDice.count(dice_msg.qq_id) ? DefaultDice[dice_msg.qq_id] : 100;
				RD rdTurnCnt(strTurnCnt, intDefaultDice);
				const int intRdTurnCntRes = rdTurnCnt.Roll();
				if (intRdTurnCntRes == Value_Err){
					dice_msg.Reply(GlobalMsg["strValueErr"]);
					return;
				}
				if (intRdTurnCntRes == Input_Err){
					dice_msg.Reply(GlobalMsg["strInputErr"]);
					return;
				}
				if (intRdTurnCntRes == ZeroDice_Err){
					dice_msg.Reply(GlobalMsg["strZeroDiceErr"]);
					return;
				}
				if (intRdTurnCntRes == ZeroType_Err){
					dice_msg.Reply(GlobalMsg["strZeroTypeErr"]);
					return;
				}
				if (intRdTurnCntRes == DiceTooBig_Err){
					dice_msg.Reply(GlobalMsg["strDiceTooBigErr"]);
					return;
				}
				if (intRdTurnCntRes == TypeTooBig_Err){
					dice_msg.Reply(GlobalMsg["strTypeTooBigErr"]);
					return;
				}
				if (intRdTurnCntRes == AddDiceVal_Err){
					dice_msg.Reply(GlobalMsg["strAddDiceValErr"]);
					return;
				}
				if (intRdTurnCntRes != 0){
					dice_msg.Reply(GlobalMsg["strUnknownErr"]);
					return;
				}
				if (rdTurnCnt.intTotal > 10){
					dice_msg.Reply(GlobalMsg["strRollTimeExceeded"]);
					return;
				}
				if (rdTurnCnt.intTotal <= 0){
					dice_msg.Reply(GlobalMsg["strRollTimeErr"]);
					return;
				}
				intTurnCnt = rdTurnCnt.intTotal;
				if (strTurnCnt.find('d') != string::npos){
					String strTurnNotice = strNickName + "的掷骰轮数: " + rdTurnCnt.FormShortString() + "轮";
					if (!isHidden)
					{
						dice_msg.Reply(strTurnNotice);
					}
					else
					{
						if (dice_msg.msg_type == Dice::MsgType::Group)
						{
							strTurnNotice = "在群\"" + getGroupList()[dice_msg.group_id] + "\"中 " + strTurnNotice;
						}
						else if (dice_msg.msg_type == Dice::MsgType::Discuss)
						{
							strTurnNotice = GlobalMsg["strHiddenDiscussPrefix"] + strTurnNotice;
						}
						AddMsgToQueue(Dice::DiceMsg(strTurnNotice, 0LL, dice_msg.qq_id, Dice::MsgType::Private));
						pair<multimap<long long, long long>::iterator, multimap<long long, long long>::iterator> range;
						if (dice_msg.msg_type == Dice::MsgType::Group)
						{
							range = ObserveGroup.equal_range(dice_msg.group_id);
						}
						else if (dice_msg.msg_type == Dice::MsgType::Discuss)
						{
							range = ObserveDiscuss.equal_range(dice_msg.group_id);
						}
						for (auto it = range.first; it != range.second; ++it)
						{
							if (it->second != dice_msg.qq_id)
							{
								AddMsgToQueue(Dice::DiceMsg(strTurnNotice, 0LL, it->second, Dice::MsgType::Private));
							}
						}
					}
				}
			}
			const int intDefaultDice = DefaultDice.count(dice_msg.qq_id) ? DefaultDice[dice_msg.qq_id] : 100;
			RD rdMainDice(strMainDice, intDefaultDice);

			const int intFirstTimeRes = rdMainDice.Roll();
			if (intFirstTimeRes == Value_Err){
				dice_msg.Reply(GlobalMsg["strValueErr"]);
				return;
			}
			if (intFirstTimeRes == Input_Err){
				dice_msg.Reply(GlobalMsg["strInputErr"]);
				return;
			}
			if (intFirstTimeRes == ZeroDice_Err){
				dice_msg.Reply(GlobalMsg["strZeroDiceErr"]);
				return;
			}
			if (intFirstTimeRes == ZeroType_Err){
				dice_msg.Reply(GlobalMsg["strZeroTypeErr"]);
				return;
			}
			if (intFirstTimeRes == DiceTooBig_Err){
				dice_msg.Reply(GlobalMsg["strDiceTooBigErr"]);
				return;
			}
			if (intFirstTimeRes == TypeTooBig_Err){
				dice_msg.Reply(GlobalMsg["strTypeTooBigErr"]);
				return;
			}
			if (intFirstTimeRes == AddDiceVal_Err){
				dice_msg.Reply(GlobalMsg["strAddDiceValErr"]);
				return;
			}
			if (intFirstTimeRes != 0){
				dice_msg.Reply(GlobalMsg["strUnknownErr"]);
				return;
			}
			if (!booleanDetail && intTurnCnt != 1){
				String strAns = strNickName + "骰出了: " + to_string(intTurnCnt) + "次" + rdMainDice.strDice + ": { ";
				if (!strReason.empty())
					strAns.insert(0, "由于" + strReason + " ");
				vector<int> vintExVal;
				while (intTurnCnt--){
					// 此处返回值无用
					// ReSharper disable once CppExpressionWithoutSideEffects
					rdMainDice.Roll();
					strAns += to_string(rdMainDice.intTotal);
					if (intTurnCnt != 0)
						strAns += ",";
					if ((rdMainDice.strDice == "D100" || rdMainDice.strDice == "1D100") && (rdMainDice.intTotal <= 5 ||
						rdMainDice.intTotal >= 96))
						vintExVal.push_back(rdMainDice.intTotal);
				}
				strAns += " }";
				if (!vintExVal.isEmpty()){
					strAns += ",极值: ";
					for (auto it = vintExVal.cbegin(); it != vintExVal.cend(); ++it){
						strAns += to_string(*it);
						if (it != vintExVal.cend() - 1)
							strAns += ",";
					}
				}
				if (!isHidden){
					dice_msg.Reply(strAns);
				}else{
					if (dice_msg.msg_type == Dice::MsgType::Group){
						strAns = "在群\"" + getGroupList()[dice_msg.group_id] + "\"中 " + strAns;
					}else if (dice_msg.msg_type == Dice::MsgType::Discuss){
						strAns = GlobalMsg["strHiddenDiscussPrefix"] + strAns;
					}
					AddMsgToQueue(Dice::DiceMsg(strAns, 0LL, dice_msg.qq_id, Dice::MsgType::Private));
					pair<multimap<long long, long long>::iterator, multimap<long long, long long>::iterator> range;					
								range = ObserveGroup.equal_range(dice_msg.group_id);
					for (auto it = range.first; it != range.second; ++it){
						if (it->second != dice_msg.qq_id){
							AddMsgToQueue(Dice::DiceMsg(strAns, 0LL, it->second, Dice::MsgType::Private));
						}
					}
				}
			}else{
				while (intTurnCnt--){
					rdMainDice.Roll();
					String strAns = strNickName + "骰出了: " + (booleanDetail? rdMainDice.FormCompleteString(): rdMainDice.FormShortString());
					if (!strReason.empty())
						strAns.insert(0, "由于" + strReason + " ");
					if (!isHidden){
						dice_msg.Reply(strAns);
					}else{
									strAns = "在群\"" + getGroupList()[dice_msg.group_id] + "\"中 " + strAns;
							AddMsgToQueue(Dice::DiceMsg(strAns, 0LL, dice_msg.qq_id, Dice::MsgType::Private));
						pair<multimap<long long, long long>::iterator, multimap<long long, long long>::iterator> range;
									range = ObserveGroup.equal_range(dice_msg.group_id);
							for (auto it = range.first; it != range.second; ++it){
							if (it->second != dice_msg.qq_id){
								AddMsgToQueue(Dice::DiceMsg(strAns, 0LL, it->second, Dice::MsgType::Private));
							}
						}
					}
				}
			}
			if (isHidden){
				dice_msg.Reply(format(GlobalMsg["strHiddenDiceReplyMsg"], {
					{ "nick", strNickName }
				}));
			}
		}
	}
	return false;
	}
}
