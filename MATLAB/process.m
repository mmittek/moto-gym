

data = csvread('2016-09-25 22_27_17_260.csv', 2);


N = 4;

figure(1)
clf
subplot(N, 1, 1);
plot( data(:, 2:4) );
title('Acceleration');

subplot(N, 1, 2);
plot( data(:, 5:7) );
title('Linear acceleration');

subplot(N, 1, 3);
plot( data(:, 8:10) );
title('Gravity');

subplot(N, 1, 4);
plot( data(:, 14:16) );
title('Orientation');



% Forward lean angle
figure(2)
clf;
plot(90+ data(:,15)*180/pi );
title('Forward angle');

figure(3)
clf;
plot( -data(:,16)*180/pi );
title('Lean angle');

% Angle data
angleData = data(:,[1 15]);
angleData(:,2) = angleData(:,2)+pi/2;

angleData(:,1) = angleData(:,1)./1000000;
idx = find(angleData(:,1) > angleData(1,1));
angleData = angleData(idx,:);

angleData(:,1) = angleData(:,1)-min(angleData(:,1));

[sx sy] = sampleUniformly(angleData(:,1),angleData(:,2));

load('lowpass.mat');
dc = filter(LowPass, sy);


Fs = 1000/(sx(2)-sx(1));

L = numel(sx);
Y = fft( sy);
P2 = abs(Y/L);
P1 = P2(1:L/2+1);
P1(2:end-1) = 2*P1(2:end-1);

f = Fs*(0:(L/2))/L;
figure(6)
plot(f,P1)
title('Single-Sided Amplitude Spectrum of X(t)')
xlabel('f (Hz)')
ylabel('|P1(f)|')

figure(5)
clf;
hold on;
plot(angleData(:,1),angleData(:,2)*180/pi, 'b');
plot(sx, (sy-dc)*180/pi , 'r');
plot( sx, dc*180/pi );
hold off;




% Animation
vec = [ 0,0,1,1 ];





