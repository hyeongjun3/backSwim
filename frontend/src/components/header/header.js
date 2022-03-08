import React from 'react';
import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';
import './header.css';
import { withRouterHook } from '#utils/withRouter';
import { DropDown, DropDownItemNode } from '#components/dropDown/dropDown';

function ItemNode(text, handler, id) {
  Object.assign(this, { text, handler, id });
}

/* TODO: Link vs useNavigate */
class Header extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showDropDown: false,
    };
  }

  setShowDropDownFalse() {
    this.setState({
      showDropDown: false,
    });
  }

  renderItemContainer() {
    function handlerSignIn(e) {
      e.preventDefault();

      this.props.navigate('/signIn');
    }

    function handlerSignUp(e) {
      e.preventDefault();

      this.props.navigate('/signUp');
    }

    function handlerMyProfile(e) {
      e.preventDefault();

      this.setState({
        showDropDown: true,
      });
    }

    const items =
      this.props.isLogin === false
        ? [
            new ItemNode('Sign In', handlerSignIn.bind(this), 'btn-signIn'),
            new ItemNode('Sign Up', handlerSignUp.bind(this), 'btn-signUp'),
          ]
        : [new ItemNode('My Profile', handlerMyProfile.bind(this), 'btn-myProfile')];

    return (
      <nav className="header-item-container">
        {items.map((value, idx) => {
          return (
            <div key={idx} id={value.id} className="item" onClick={value.handler}>
              {value.text}
            </div>
          );
        })}
      </nav>
    );
  }

  renderDropDown() {
    if (this.state.showDropDown === false) return;

    function handlerShowProfile(e) {
      e.preventDefault();

      console.log('handler show profile');
    }

    function handlerSignOut(e) {
      e.preventDefault();

      console.log('hello');
    }

    const items = [
      new DropDownItemNode('프로필 보기', handlerShowProfile.bind(this)),
      new DropDownItemNode('로그아웃', handlerSignOut.bind(this)),
    ];

    return <DropDown items={items} closeHandler={this.setShowDropDownFalse.bind(this)}></DropDown>;
  }

  render() {
    return (
      <>
        <div className="header">
          <Link to={'/'} className="header-logo-link">
            <i className="header-logo fas fa-swimmer"></i>
          </Link>
          {this.renderItemContainer()}
        </div>
        {this.renderDropDown()}
      </>
    );
  }
}

Header.propTypes = {
  isLogin: PropTypes.bool.isRequired,
  navigate: PropTypes.func.isRequired,
};

export default withRouterHook(Header);
