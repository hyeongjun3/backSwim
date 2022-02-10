import React, { Component } from 'react';
import './sideBarText.css';
import MyRequest from 'Common/myRequest';
import debounce from 'Utils/debounce';
import { PoolNode, RangeNode } from 'Common/myNodes';
import { INC_DEC } from 'Common/myTypes';

export default class SideBarText extends Component {
  selectedItemIdx = -1;

  constructor(props) {
    super(props);
    this.state = {
      searchResult: [], // PoolNode
    };
  }

  /**
   *
   * range에 벗어나지 않게 값을 더하거나 뺌
   * @param {INC_DEC} type
   * @param {number} value
   * @param {RangeNode} range
   * @return {number}
   * @memberof SideBarText
   */
  incDecValue(type, value, range) {
    if (type == INC_DEC.INCREASE) {
      value = Math.min(value + 1, range.finish);
    } else if (type == INC_DEC.DECREASE) {
      value = Math.max(value - 1, range.start);
    }

    return value;
  }

  /**
   *
   * parent element의 targetIdx번 째 child의 style을 focuse해주고 스크롤을 이동
   * @param {HTMLElement} parentEl
   * @param {number} targetIdx
   * @memberof SideBarText
   */
  focusItem(parentEl, targetIdx) {
    for (let i = 0; i < parentEl.children.length; i++) {
      const child = parentEl.children[i];
      if (i === targetIdx) {
        child.classList.add('focus-bg-change');
        parentEl.scrollTop = child.offsetTop - parentEl.children[0].offsetTop;
      } else {
        child.classList.remove('focus-bg-change');
      }
    }
  }

  /**
   *
   * input change event로 추천 검색어 탐색
   * state change: searchResult
   * @param {*} event
   * @return {void}
   * @memberof SideBarText
   */
  handlerChange(event) {
    if (event.target.value.length === 0) {
      return;
    }

    MyRequest.getPoolsByName(event.target.value).then((res) => {
      const pools = res.data.pool.map((value) => new PoolNode(value));
      this.setState({
        searchResult: pools,
      });
    });
  }

  handlerKeyDown(event) {
    /* arrow up(38) and down(40) */
    if (event.keyCode === 40 || event.keyCode === 38) {
      const searchListEl = document.querySelector('.side-bar-search-list');
      if (searchListEl === null) return;

      const type = event.keyCode === 40 ? INC_DEC.INCREASE : INC_DEC.DECREASE;
      this.selectedItemIdx = this.incDecValue(
        type,
        this.selectedItemIdx,
        new RangeNode(0, searchListEl.children.length - 1)
      );

      this.focusItem(searchListEl, this.selectedItemIdx);
    }
  }

  /**
   *
   * 추천 검색어 리스트를 렌더링 하는 함수
   * @param {*} searchResult
   * @return {*} HTMLElement
   * @memberof SideBarText
   */
  renderSearchResult(searchResult) {
    if (searchResult.length === 0) {
      return;
    }

    return (
      <div className="side-bar-search-list" onClick={(e) => this.handlerSearchListKeyDown(e)}>
        {searchResult.map((value, idx) => (
          <div className="side-bar-serach-item" key={idx}>
            {value.placeName}
          </div>
        ))}
      </div>
    );
  }

  render() {
    return (
      <div className="side-bar-text" onKeyDown={(e) => this.handlerKeyDown(e)}>
        <div className="side-bar-search-bar">
          <button className="search-button">
            <i className="fas fa-search"></i>
          </button>
          <input
            id="side-bar-search-engine"
            type="text"
            placeholder="수영장 이름을 입력해주세요"
            autoComplete="off"
            onChange={debounce((e) => this.handlerChange(e))}
          />
        </div>
        {this.renderSearchResult(this.state.searchResult)}
      </div>
    );
  }
}
