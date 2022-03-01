import React from 'react';
import './emailSent.css';
import debounce from '#utils/debounce';
import PropTypes from 'prop-types';
import { withLocation } from '#utils/withRouter';
import MyRequest from '#common/myRequest';

class EmailSent extends React.Component {
  constructor(props) {
    super(props);
  }

  handlerSentBtn() {
    const { email, password } = this.props.locationState;

    MyRequest.signUp(email, password)
      .then((value) => {
        console.log(value);
      })
      .catch((err) => {
        console.log(err);
      });
  }

  render() {
    if (this.props.locationState === null) {
      return <div>잘못된 접근 입니다.</div>;
    }

    return (
      <div className="email-sent-container">
        <span>{this.props.locationState.email}로 인증 메일이 전송되었습니다</span>
        <button onClick={debounce((e) => this.handlerSentBtn(e).bind(this), 200)}>재전송</button>
      </div>
    );
  }
}

EmailSent.propTypes = {
  locationState: PropTypes.object,
};

export default withLocation(EmailSent);
