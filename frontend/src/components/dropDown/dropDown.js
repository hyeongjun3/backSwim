import React from 'react';
import PropTypes from 'prop-types';
import './dropDown.css';

function DropDownItemNode(text, handler) {
  Object.assign(this, { text, handler });
}

/* TODO: ref 공부 필요*/
class DropDown extends React.Component {
  constructor(props) {
    super(props);

    this.wrapperRef = React.createRef();
  }

  componentDidMount() {
    document.addEventListener('mousedown', this.handlerClickOutside.bind(this));
  }

  componentWillUnmount() {
    document.removeEventListener('mousedown', this.handlerClickOutside.bind(this));
  }

  renderItems() {
    const items = this.props.items;
    console.log(items);

    return items.map((value, idx) => (
      <div key={idx} className="drop-down-item" onClick={value.handler}>
        {value.text}
      </div>
    ));
  }

  handlerClickOutside(e) {
    if (this.wrapperRef && this.wrapperRef.current && !this.wrapperRef.current.contains(e.target)) {
      this.props.closeHandler();
    }
  }

  render() {
    return (
      <div ref={this.wrapperRef} className="drop-down-container">
        {this.renderItems()}
      </div>
    );
  }
}

DropDown.propTypes = {
  closeHandler: PropTypes.func.isRequired,
  items: PropTypes.arrayOf(PropTypes.instanceOf(DropDownItemNode)).isRequired,
};

export { DropDown, DropDownItemNode };
